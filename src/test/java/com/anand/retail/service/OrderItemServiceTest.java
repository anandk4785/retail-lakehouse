package com.anand.retail.service;

import com.anand.retail.reader.OrderItemReader;
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

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class OrderItemServiceTest {

    private static SparkSession spark;

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("OrderItemServiceTest")
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
    void testLoadAndShowOrderItemsLogsOutput() {
        try {
            OrderItemReader orderItemReader = new OrderItemReader();
            OrderItemService orderItemService = new OrderItemService(orderItemReader);

            Dataset<Row> orderItemDf = orderItemService.loadAndShowOrderItems(spark);

            assertNotNull(orderItemDf, "Result DataFrame should not be null");
            assertTrue(orderItemDf.columns().length > 0, "Result DataFrame should have columns");
        } catch (Exception e) {
            fail("Service method should not throw exception: " + e.getMessage());
        }
    }

    @Test
    void testManualDataFrameProcessing() {
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("order_item_id", DataTypes.StringType, false),
                DataTypes.createStructField("order_id", DataTypes.StringType, true),
                DataTypes.createStructField("product_id", DataTypes.StringType, true),
                DataTypes.createStructField("seller_id", DataTypes.StringType, true),
                DataTypes.createStructField("shipping_limit_date", DataTypes.TimestampType, true),
                DataTypes.createStructField("price", DataTypes.DoubleType, true),
                DataTypes.createStructField("freight_value", DataTypes.DoubleType, true)
        });

        List<Row> data = List.of(
                RowFactory.create("OI-001", "O-001", "P-001", "S-001", Timestamp.valueOf("2024-01-01 10:00:00"), 100.0, 10.0),
                RowFactory.create("OI-002", "O-002", "P-002", "S-002", Timestamp.valueOf("2024-01-02 11:00:00"), 200.0, 20.0)
        );

        Dataset<Row> testDf = spark.createDataFrame(data, schema);

        assertEquals(2, testDf.count(), "DataFrame should have 2 rows");
        assertEquals(7, testDf.columns().length,
                "DataFrame should have 7 columns");

        testDf.show(5, false);

        testDf.printSchema();
    }
}
