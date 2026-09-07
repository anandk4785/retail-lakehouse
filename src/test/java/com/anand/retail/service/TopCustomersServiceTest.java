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
 * Integration test for TopCustomersService, mirroring
 * SalesAnalyticsServiceTest's shape: a real SparkSessionFactory session,
 * real Silver Hive tables seeded via SilverWriter and registered via
 * HiveRegistrar, and the real Gold managed-table write verified
 * afterward through Spark SQL.
 *
 * buildTopCustomers() reads directly from the Hive catalog by design
 * (see TopCustomersService's Javadoc / ADR-023) — same accepted
 * testability trade-off as SalesAnalyticsService.
 *
 * NOTE — no @AfterAll spark.stop(), same as HiveRegistrarTest/
 * SalesAnalyticsServiceTest. Shared SparkSessionFactory singleton; see
 * ADR-021 Lessons Learned.
 */
public class TopCustomersServiceTest {

    private static SparkSession spark;

    @BeforeAll
    static void setup() {
        spark = SparkSessionFactory.getSparkSession();

        createDatabase("silver", ConfigLoader.get("silver.path"));
        createDatabase("gold", ConfigLoader.get("gold.path"));

        seedCustomers();
        seedOrders();
        seedOrderItems();
    }

    private static void createDatabase(String database, String path) {
        String locationUri = new File(path).toURI().toString();
        spark.sql("CREATE DATABASE IF NOT EXISTS " + database + " LOCATION '" + locationUri + "'");
    }

    private static void seedCustomers() {
        StructType schema = new StructType()
                .add("customer_id", DataTypes.StringType, true)
                .add("customer_city", DataTypes.StringType, true)
                .add("customer_state", DataTypes.StringType, true);

        List<Row> rows = Arrays.asList(
                RowFactory.create("C-001", "Sao Paulo", "SP"),
                RowFactory.create("C-002", "Rio De Janeiro", "RJ")
        );

        Dataset<Row> df = spark.createDataFrame(rows, schema);
        new SilverWriter().writeTable(df, LakehouseTable.CUSTOMERS);
        new HiveRegistrar().register(spark, HiveTable.SILVER_CUSTOMERS);
    }

    private static void seedOrders() {
        StructType schema = new StructType()
                .add("order_id", DataTypes.StringType, true)
                .add("customer_id", DataTypes.StringType, true)
                .add("is_delivered", DataTypes.BooleanType, true);

        List<Row> rows = Arrays.asList(
                RowFactory.create("O-001", "C-001", true),
                RowFactory.create("O-002", "C-001", true),
                RowFactory.create("O-003", "C-002", true),
                RowFactory.create("O-004", "C-002", false) // not delivered — must be excluded
        );

        Dataset<Row> df = spark.createDataFrame(rows, schema);
        new SilverWriter().writeTable(df, LakehouseTable.ORDERS);
        new HiveRegistrar().register(spark, HiveTable.SILVER_ORDERS);
    }

    private static void seedOrderItems() {
        StructType schema = new StructType()
                .add("order_id", DataTypes.StringType, true)
                .add("order_item_id", DataTypes.LongType, true)
                .add("price", DataTypes.DoubleType, true)
                .add("freight_value", DataTypes.DoubleType, true);

        List<Row> rows = Arrays.asList(
                RowFactory.create("O-001", 1L, 100.0, 10.0),
                RowFactory.create("O-002", 1L, 50.0, 5.0),
                RowFactory.create("O-003", 1L, 500.0, 50.0),
                RowFactory.create("O-004", 1L, 999.0, 99.0) // order not delivered — must be excluded
        );

        Dataset<Row> df = spark.createDataFrame(rows, schema);
        new SilverWriter().writeTable(df, LakehouseTable.ORDER_ITEMS);
        new HiveRegistrar().register(spark, HiveTable.SILVER_ORDER_ITEMS);
    }

    @Test
    void shouldBuildAndPersistTopCustomersRankedByTotalSpentExcludingNonDeliveredOrders() {
        TopCustomersService service = new TopCustomersService(
                new HiveWriter(), "gold", "top_customers"
        );

        service.run(spark);

        List<Row> tables = spark.sql("SHOW TABLES IN gold").collectAsList();
        boolean tablePresent = tables.stream()
                .anyMatch(row -> "top_customers".equalsIgnoreCase(row.getAs("tableName")));
        assertTrue(tablePresent, "gold.top_customers should appear in SHOW TABLES IN gold");

        Dataset<Row> result = spark.sql("SELECT * FROM gold.top_customers ORDER BY customer_rank");

        assertEquals(2, result.count(),
                "Only C-001 and C-002 should appear (both have at least one delivered order)");

        List<Row> rows = result.collectAsList();

        Row first = rows.get(0); // rank 1 = highest total_spent
        assertEquals("C-002", first.getAs("customer_id"));
        assertEquals(1L, (long) first.getAs("order_count"));
        assertEquals(550.0, (double) first.getAs("total_spent"), 0.0001,
                "C-002's total_spent should be 500 + 50 from O-003 only, excluding O-004's 999 + 99");
        assertEquals(550.0, (double) first.getAs("avg_order_value"), 0.0001);
        assertEquals(1, (int) first.getAs("customer_rank"));

        Row second = rows.get(1);
        assertEquals("C-001", second.getAs("customer_id"));
        assertEquals(2L, (long) second.getAs("order_count"));
        assertEquals(165.0, (double) second.getAs("total_spent"), 0.0001,
                "C-001's total_spent should sum O-001 (110) and O-002 (55)");
        assertEquals(82.5, (double) second.getAs("avg_order_value"), 0.0001);
        assertEquals(2, (int) second.getAs("customer_rank"));
    }

    @Test
    void shouldOverwriteCleanlyOnRerunRatherThanAccumulate() {
        TopCustomersService service = new TopCustomersService(
                new HiveWriter(), "gold", "top_customers"
        );

        service.run(spark);
        service.run(spark);

        long rowCount = spark.sql("SELECT * FROM gold.top_customers").count();
        assertEquals(2, rowCount,
                "SaveMode.Overwrite should replace the table's contents on rerun, not append duplicates");
    }
}