package com.anand.retail.service;

import com.anand.retail.reader.PaymentReader;
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

public class PaymentServiceTest {

    private static SparkSession spark;

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("PaymentServiceTest")
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
    void testLoadAndShowPaymentsLogsOutput() {
        Logger logger = LoggerFactory.getLogger(PaymentServiceTest.class);

        try {
            final PaymentReader paymentReader = new PaymentReader();
            final PaymentService paymentService =
                    new PaymentService(paymentReader);
            Dataset<Row> resultDf = paymentService.loadAndShowPayments(spark);

            assertNotNull(resultDf, "Result dataframe should not be null");
            assertTrue(
                    resultDf.columns().length > 0,
                    "Result dataframe should have columns"
            );
        } catch (Exception e) {
            fail("Service method should not throw exception: " + e.getMessage());
        }

    }

    @Test
    void testManualDataFrameProcessing() {
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("order_id", DataTypes.StringType, true),
                DataTypes.createStructField("payment_type", DataTypes.StringType, true),
                DataTypes.createStructField("payment_value", DataTypes.DoubleType, true)
        });

        List<Row> rows = Arrays.asList(
                RowFactory.create("1", "credit_card", 100.0),
                RowFactory.create("2", "cash", 50.0)
        );

        Dataset<Row> testDf = spark.createDataFrame(rows, schema);

        assertEquals(2, testDf.count(), "DataFrame should have 2 rows");
        assertEquals(3, testDf.columns().length, "DataFrame should have 3 columns");

        testDf.show(5, false);

        testDf.printSchema();
    }
}
