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

public class OrderReaderTest {
    private static SparkSession spark;

    private static final OrderReader orderReader = new OrderReader();

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("OrderReaderTest")
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
    void shouldReadOrderDataset() {
        Dataset<Row> orderDf = orderReader.readOrders(spark);

        assertNotNull(orderDf);
        assertTrue(orderDf.count() > 0);
    }

    @Test
    void shouldContainExpectedColumns() {
        Dataset<Row> orderDf = orderReader.readOrders(spark);
        List<String> columns = Arrays.asList(orderDf.columns());

        assertTrue(columns.contains("order_id"));
        assertTrue(columns.contains("customer_id"));
        assertTrue(columns.contains("order_status"));
    }
}