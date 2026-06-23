package com.anand.retail.writer;

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
    private static final String BRONZE_TEST_PATH = "./data/test/bronze/customers";

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
    void testWriteCustomers() {
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

        // Call writeCustomers
        bronzeWriter.writeCustomers(customerDf);

        // Verify: Check if files exist
        assertTrue(Files.exists(Paths.get(BRONZE_TEST_PATH)),
                "Bronze customers directory should exist");

        // Verify: Read back and check row count
        Dataset<Row> readDf = spark.read()
                .parquet(BRONZE_TEST_PATH);

        long count = readDf.count();
        assertEquals(3, count, "Should have written 3 rows");

        // Verify: Check column count
        int columnCount = readDf.columns().length;
        assertEquals(5, columnCount, "Should have 5 columns");
    }
}
