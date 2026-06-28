package com.anand.retail.service;

import com.anand.retail.reader.OrderReader;
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
import java.util.Arrays;
import java.util.List;

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
    void testLoadAndShowOrdersLogsOutput() {
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

    @Test
    void testManualDataFrameProcessing() {
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("order_id", DataTypes.StringType, false),
                DataTypes.createStructField("customer_id", DataTypes.StringType, false),
                DataTypes.createStructField("order_purchase_timestamp", DataTypes.TimestampType, false)
        });

        List<Row> rows = Arrays.asList(
                RowFactory.create("order1", "customer1", Timestamp.valueOf("2017-10-02 10:15:30")),
                RowFactory.create("order2", "customer2", Timestamp.valueOf("2017-10-03 11:20:45")),
                RowFactory.create("order3", "customer3", Timestamp.valueOf("2017-10-04 12:15:22"))
        );

        Dataset<Row> testDf = spark.createDataFrame(rows, schema);

        assertEquals(3, testDf.count(), "DataFrame should have 3 rows");
        assertEquals(3, testDf.columns().length, "DataFrame should have 3 columns");

        testDf.show(5, false);
        testDf.printSchema();
    }
}