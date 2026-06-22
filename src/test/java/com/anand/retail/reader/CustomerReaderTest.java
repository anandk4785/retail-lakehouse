package com.anand.retail.reader;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomerReaderTest {
    private static SparkSession spark;

    private static final CustomerReader customerReader =
            new CustomerReader();

    @BeforeAll
    static void setup() {
        spark =
                SparkSession
                        .builder()
                        .appName("CustomerReaderTest")
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
    void shouldReadCustomerDataset() {
        Dataset<Row> customerDf =
                customerReader
                        .readCustomers(spark);

        assertNotNull(
                customerDf
        );

        assertTrue(
                customerDf.count() > 0
        );
    }

    @Test
    void shouldContainExpectedColumns() {
        Dataset<Row> customerDf =
                customerReader
                        .readCustomers(spark);

        List<String> columns =
                Arrays.asList(
                        customerDf.columns()
                );

        assertTrue(
                columns.contains(
                        "customer_city"
                )
        );

        assertTrue(
                columns.contains(
                        "customer_state"
                )
        );

        assertTrue(
                columns.contains(
                        "customer_id"
                )
        );
    }
}
