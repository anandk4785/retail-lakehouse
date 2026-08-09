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

public class OrderItemReaderTest {
    private static SparkSession spark;

    private static final OrderItemReader orderItemReader = new OrderItemReader();

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("OrderItemReaderTest")
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
    void shouldReadOrderItemDataset() {
        Dataset<Row> orderItemDf = orderItemReader.readOrderItem(spark);

        assertNotNull(orderItemDf);
        assertTrue(orderItemDf.count() > 0);
    }

    @Test
    void shouldContainExpectedColumns() {
        Dataset<Row> orderItemDf = orderItemReader.readOrderItem(spark);
        List<String> columns = Arrays.asList(orderItemDf.columns());

        assertTrue(columns.contains("order_item_id"));
        assertTrue(columns.contains("order_id"));
        assertTrue(columns.contains("freight_value"));
    }
}
