package com.anand.retail.service;

import com.anand.retail.reader.PaymentReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentService {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    PaymentService.class);

    private final PaymentReader paymentReader;

    public PaymentService(PaymentReader paymentReader) {
        this.paymentReader = paymentReader;
    }

    public Dataset<Row> loadAndShowPayments(SparkSession spark) {
        Dataset<Row> paymentDf = paymentReader.readPayments(spark);

        logger.info("Printing schema for payments");
        paymentDf.printSchema();

        logger.info("Showing sample payment records");
        paymentDf.show(10, false);

        long count = paymentDf.count();
        logger.info("Payment count : {}", count);

        return paymentDf;
    }
}
