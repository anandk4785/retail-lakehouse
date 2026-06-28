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

public class PaymentReaderTest {

    private static SparkSession spark;

    private static final PaymentReader paymentReader =
            new PaymentReader();

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("PaymentReaderTest")
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
    void shouldReadPaymentDataset() {
        Dataset<Row> paymentDf = paymentReader.readPayments(spark);

        assertNotNull(paymentDf);
        assertTrue(
                paymentDf.count() > 0
        );
    }

    @Test
    void shouldContainExpectedColumns() {
        Dataset<Row> paymentDf = paymentReader.readPayments(spark);

        List<String> columns = Arrays.asList(paymentDf.columns());

        assertTrue(columns.contains("order_id"));
        assertTrue(columns.contains("payment_type"));
        assertTrue(columns.contains("payment_value"));
    }
}
