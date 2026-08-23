package com.anand.retail.service;

import com.anand.retail.config.ConfigLoader;
import com.anand.retail.constants.HiveTable;
import com.anand.retail.constants.LakehouseTable;
import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.writer.HiveWriter;
import com.anand.retail.writer.SilverWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for SalesAnalyticsService, mirroring HiveRegistrarTest's
 * shape: a real SparkSessionFactory session, real Silver Hive tables seeded
 * via SilverWriter and registered via HiveRegistrar, and the real Gold
 * managed-table write verified afterward through Spark SQL.
 *
 * buildMonthlyProductSales() reads directly from the Hive catalog by
 * design (see SalesAnalyticsService's Javadoc) — it cannot be exercised
 * with small synthetic DataFrames the way *TransformerTest/*ValidatorTest
 * can. This test pays that cost deliberately, combining aggregation
 * correctness with the HiveWriter managed-table round-trip proof in one
 * place, rather than as two separate tests.
 *
 * NOTE — no @AfterAll spark.stop(), same as HiveRegistrarTest. This test
 * uses the shared SparkSessionFactory singleton; stopping it would break
 * any later test in the same JVM that calls getSparkSession() again. See
 * ADR-021 Lessons Learned.
 */
public class SalesAnalyticsServiceTest {

    private static SparkSession spark;

    @BeforeAll
    static void setup() {
        spark = SparkSessionFactory.getSparkSession();

        createDatabase("silver", ConfigLoader.get("silver.path"));
        createDatabase("gold", ConfigLoader.get("gold.path"));

        seedOrderItems();
        seedOrders();
        seedProducts();
    }

    private static void createDatabase(String database, String path) {
        String locationUri = new File(path).toURI().toString();
        spark.sql("CREATE DATABASE IF NOT EXISTS " + database + " LOCATION '" + locationUri + "'");
    }

    private static void seedOrderItems() {
        StructType schema = new StructType()
                .add("order_id", DataTypes.StringType, true)
                .add("order_item_id", DataTypes.LongType, true)
                .add("product_id", DataTypes.StringType, true)
                .add("price", DataTypes.DoubleType, true)
                .add("freight_value", DataTypes.DoubleType, true);

        List<Row> rows = Arrays.asList(
                RowFactory.create("O-001", 1L, "P-001", 100.0, 10.0), // delivered, electronics
                RowFactory.create("O-001", 2L, "P-002", 50.0, 5.0),   // delivered, toys
                RowFactory.create("O-002", 1L, "P-001", 200.0, 20.0), // delivered, electronics
                RowFactory.create("O-003", 1L, "P-001", 999.0, 99.0)  // NOT delivered — must be excluded
        );

        Dataset<Row> df = spark.createDataFrame(rows, schema);
        new SilverWriter().writeTable(df, LakehouseTable.ORDER_ITEMS);
        new HiveRegistrar().register(spark, HiveTable.SILVER_ORDER_ITEMS);
    }

    private static void seedOrders() {
        StructType schema = new StructType()
                .add("order_id", DataTypes.StringType, true)
                .add("purchase_year", DataTypes.IntegerType, true)
                .add("purchase_month", DataTypes.IntegerType, true)
                .add("is_delivered", DataTypes.BooleanType, true);

        List<Row> rows = Arrays.asList(
                RowFactory.create("O-001", 2018, 1, true),
                RowFactory.create("O-002", 2018, 1, true),
                RowFactory.create("O-003", 2018, 1, false) // canceled/in-flight
        );

        Dataset<Row> df = spark.createDataFrame(rows, schema);
        new SilverWriter().writeTable(df, LakehouseTable.ORDERS);
        new HiveRegistrar().register(spark, HiveTable.SILVER_ORDERS);
    }

    private static void seedProducts() {
        StructType schema = new StructType()
                .add("product_id", DataTypes.StringType, true)
                .add("product_category_name", DataTypes.StringType, true);

        List<Row> rows = Arrays.asList(
                RowFactory.create("P-001", "electronics"),
                RowFactory.create("P-002", "toys")
        );

        Dataset<Row> df = spark.createDataFrame(rows, schema);
        new SilverWriter().writeTable(df, LakehouseTable.PRODUCTS);
        new HiveRegistrar().register(spark, HiveTable.SILVER_PRODUCTS);
    }

    @Test
    void shouldBuildAndPersistMonthlyProductSalesExcludingNonDeliveredOrders() {
        SalesAnalyticsService service = new SalesAnalyticsService(
                new HiveWriter(), "gold", "monthly_product_sales"
        );

        service.run(spark);

        // --- Verify the managed table round-trip through the real catalog ---
        List<Row> tables = spark.sql("SHOW TABLES IN gold").collectAsList();
        boolean tablePresent = tables.stream()
                .anyMatch(row -> "monthly_product_sales".equalsIgnoreCase(row.getAs("tableName")));
        assertTrue(tablePresent, "gold.monthly_product_sales should appear in SHOW TABLES IN gold");

        Dataset<Row> result = spark.sql("SELECT * FROM gold.monthly_product_sales");

        // O-003 is not delivered, so only O-001/O-002's items contribute —
        // 2 groups: electronics (2 items across O-001+O-002) and toys (1 item)
        assertEquals(2, result.count(), "Only groups from delivered orders should appear");

        Row electronics = result.filter("product_category_name = 'electronics'").first();
        assertEquals(2L, (long) electronics.getAs("item_count"));
        assertEquals(300.0, (double) electronics.getAs("product_revenue"), 0.0001,
                "electronics should sum O-001's P-001 (100) and O-002's P-001 (200), excluding O-003's 999");
        assertEquals(30.0, (double) electronics.getAs("freight_revenue"), 0.0001);
        assertEquals(330.0, (double) electronics.getAs("total_sales_amount"), 0.0001);
        assertEquals(150.0, (double) electronics.getAs("average_item_price"), 0.0001);

        Row toys = result.filter("product_category_name = 'toys'").first();
        assertEquals(1L, (long) toys.getAs("item_count"));
        assertEquals(50.0, (double) toys.getAs("product_revenue"), 0.0001);
    }

    @Test
    void shouldOverwriteCleanlyOnRerunRatherThanAccumulate() {
        SalesAnalyticsService service = new SalesAnalyticsService(
                new HiveWriter(), "gold", "monthly_product_sales"
        );

        service.run(spark);
        service.run(spark);

        long rowCount = spark.sql("SELECT * FROM gold.monthly_product_sales").count();
        assertEquals(2, rowCount,
                "SaveMode.Overwrite should replace the table's contents on rerun, not append duplicates");
    }
}