package com.anand.retail.main;

import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.reader.BronzeReader;
import com.anand.retail.service.PaymentSilverService;
import com.anand.retail.transform.PaymentTransformer;
import com.anand.retail.validator.PaymentValidator;
import com.anand.retail.writer.SilverWriter;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentSilverJob {

    private static final Logger logger = LoggerFactory.getLogger(PaymentSilverJob.class);

    public static void main(String[] args) {
        logger.info("Starting Payment Silver Job");

        SparkSession spark = SparkSessionFactory.getSparkSession();

        try {
            PaymentSilverService service = new PaymentSilverService(
                    new BronzeReader(),
                    new PaymentTransformer(),
                    new PaymentValidator(),
                    new SilverWriter()
            );

            service.run(spark);

            logger.info("Payment Silver Fact Job Completed successfully.");
        } catch (Exception e) {
            logger.error("Payment Silver Job failed fatally!", e);

            throw new RuntimeException(e);
        } finally {
            if (spark != null) {
                spark.stop();
                logger.info("Spark Session Stopped.");
            }
        }
    }
}
