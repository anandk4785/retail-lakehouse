package com.anand.retail.writer;

import com.anand.retail.config.ConfigLoader;
import com.anand.retail.constants.LakehouseTable;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BronzeWriterTest {

    private static SparkSession spark;
    private static final String BRONZE_CUSTOMERS_PATH = Paths.get(
            ConfigLoader.get("bronze.path"),
            LakehouseTable.CUSTOMERS.getDirectoryName()
    ).toString();
    private static final String BRONZE_ORDERS_PATH = Paths.get(
            ConfigLoader.get("bronze.path"),
            LakehouseTable.ORDERS.getDirectoryName()
    ).toString();

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("BronzeWriterTest")
                .master("local[1]")
                .config("spark.ui.enabled", "false")
                .getOrCreate();
    }

    @AfterAll
    static void tearDown() {
        if (spark != null) {
            spark.stop();
        }
    }

    @Test
    void testWriteTableForCustomers() {
        BronzeWriter bronzeWriter = new BronzeWriter();

        // Create schema manually
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("customer_id", DataTypes.StringType, false),
                DataTypes.createStructField("customer_unique_id", DataTypes.StringType, false),
                DataTypes.createStructField("customer_zip_code_prefix", DataTypes.StringType, false),
                DataTypes.createStructField("customer_city", DataTypes.StringType, false),
                DataTypes.createStructField("customer_state", DataTypes.StringType, false)
        });

        // Create sample data
        List<Row> rows = Arrays.asList(
                RowFactory.create("c001", "unique001", "12345", "NYC", "NY"),
                RowFactory.create("c002", "unique002", "67890", "LA", "CA"),
                RowFactory.create("c003", "unique003", "54321", "Chicago", "IL")
        );

        // Create DataFrame
        Dataset<Row> customerDf = spark.createDataFrame(rows, schema);

        // Call the universal Enum-based write method
        bronzeWriter.writeTable(customerDf, LakehouseTable.CUSTOMERS);

        // Verify: Check if files exist
        assertTrue(Files.exists(Paths.get(BRONZE_CUSTOMERS_PATH)),
                "Bronze customers directory should exist");

        // Verify: Read back and check row count
        Dataset<Row> readDf = spark.read()
                .parquet(BRONZE_CUSTOMERS_PATH);

        long count = readDf.count();
        assertEquals(3, count, "Should have written 3 rows");

        // Verify: Check column count
        int columnCount = readDf.columns().length;
        assertEquals(5, columnCount, "Should have 5 columns");
    }

    @Test
    void testWriteTableForOrders() {
        BronzeWriter bronzeWriter = new BronzeWriter();

        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("order_id", DataTypes.StringType, false),
                DataTypes.createStructField("customer_id", DataTypes.StringType, false),
                DataTypes.createStructField("order_status", DataTypes.StringType, false)
        });

        List<Row> rows = Arrays.asList(
                RowFactory.create("o001", "c001", "delivered"),
                RowFactory.create("o002", "c002", "shipped")
        );

        Dataset<Row> orderDf = spark.createDataFrame(rows, schema);

        bronzeWriter.writeTable(orderDf, LakehouseTable.ORDERS);

        assertTrue(Files.exists(Paths.get(BRONZE_ORDERS_PATH)),
                "Bronze orders directory should exist");

        Dataset<Row> readDf = spark.read().parquet(BRONZE_ORDERS_PATH);
        assertEquals(2, readDf.count(), "Should have written 2 rows");
    }
}
