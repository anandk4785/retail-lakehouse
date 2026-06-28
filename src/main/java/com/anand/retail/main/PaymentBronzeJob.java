package com.anand.retail.main;

import com.anand.retail.constants.LakehouseTable;
import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.reader.PaymentReader;
import com.anand.retail.service.PaymentService;
import com.anand.retail.writer.BronzeWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentBronzeJob {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    PaymentBronzeJob.class
            );

    public static void main(String[] args) {
        logger.info("Starting Payment Bronze Ingestion Job");

        SparkSession spark = SparkSessionFactory.getSparkSession();
        try {

            PaymentReader paymentReader = new PaymentReader();
            PaymentService paymentService = new PaymentService(paymentReader);

            Dataset<Row> paymentDf = paymentService.loadAndShowPayments(spark);

            BronzeWriter bronzeWriter = new BronzeWriter();
            bronzeWriter.writeTable(paymentDf, LakehouseTable.PAYMENTS);

            logger.info("Payment Bronze Job Completed Successfully.");
        } catch (Exception e) {
            logger.error("Payment Bronze Job failed fatally!");

            throw new RuntimeException(e);
        } finally {
            if (spark != null) {
                spark.stop();
                logger.info("Spark Session stopped.");
            }
        }
    }
}
