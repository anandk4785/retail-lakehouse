package com.anand.retail.service;

import com.anand.retail.reader.OrderReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {

    private static SparkSession spark;

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("OrderServiceTest")
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
    void testLoadAndShowOrders() {
        try {
            // Enterprise DI Pattern
            OrderReader reader = new OrderReader();
            OrderService service = new OrderService(reader);

            Dataset<Row> resultDf = service.loadAndShowOrders(spark);

            assertNotNull(resultDf, "Result DataFrame should not be null");
            assertTrue(resultDf.columns().length > 0, "Result DataFrame should have columns");

        } catch (Exception e) {
            fail("Service method should not throw exception: " + e.getMessage());
        }
    }
}