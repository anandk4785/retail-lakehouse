package com.anand.retail.service;

import com.anand.retail.reader.CustomerReader;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerServiceTest {

    private static SparkSession spark;

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("CustomerJobHelperTest")
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
    void testLoadAndShowCustomersLogsOutput() {
        Logger logger = LoggerFactory.getLogger(CustomerServiceTest.class);

        // This test verifies that the helper/service method executes without throwing exceptions
        // The method reads actual customer data, prints schema and shows sample records
        // Note: This test assumes the data file exists in the configured path

        try {
            // Enterprise DI Pattern: Inject the reader into the service
            CustomerReader reader = new CustomerReader();
            CustomerService service = new CustomerService(reader);

            // Call the helper method - this will read from actual data source
            Dataset<Row> resultDf = service.loadAndShowCustomers(spark);

            // Verify that the result is not null
            assertNotNull(resultDf, "Result DataFrame should not be null");

            // Verify that the result has data (or at least columns)
            assertTrue(resultDf.columns().length > 0, "Result DataFrame should have columns");
        } catch (Exception e) {
            // If data is not available, skip this test or fail appropriately
            fail("Helper method should not throw exception: " + e.getMessage());
        }
    }

    @Test
    void testManualDataFrameProcessing() {
        // This test demonstrates creating and processing a DataFrame similar to what helper does
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("customer_id", DataTypes.StringType, false),
                DataTypes.createStructField("customer_city", DataTypes.StringType, false),
                DataTypes.createStructField("customer_state", DataTypes.StringType, false)
        });

        List<Row> rows = Arrays.asList(
                RowFactory.create("c001", "NYC", "NY"),
                RowFactory.create("c002", "LA", "CA"),
                RowFactory.create("c003", "Chicago", "IL")
        );

        Dataset<Row> testDf = spark.createDataFrame(rows, schema);

        // Verify the schema and data
        assertEquals(3, testDf.count(), "Should have 3 rows");
        assertEquals(3, testDf.columns().length, "Should have 3 columns");

        // Test showing records (similar to what helper does)
        testDf.show(5, false);

        // Test printing schema (similar to what helper does)
        testDf.printSchema();
    }
}



