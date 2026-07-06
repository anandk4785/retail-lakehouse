package com.anand.retail.main;

import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.reader.BronzeReader;
import com.anand.retail.service.OrderSilverService;
import com.anand.retail.transform.OrderTransformer;
import com.anand.retail.validator.OrderValidator;
import com.anand.retail.writer.SilverWriter;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderSilverJob {
    private static final Logger logger = LoggerFactory.getLogger(OrderSilverJob.class);

    public static void main(String[] args) {
        logger.info("Starting Order Silver Fact Job");

        SparkSession spark = SparkSessionFactory.getSparkSession();

        try {

            OrderSilverService service = new OrderSilverService(
                    new BronzeReader(),
                    new OrderTransformer(),
                    new OrderValidator(),
                    new SilverWriter()
            );

            service.run(spark);

            logger.info("Order Silver Fact Job Completed successfully.");
        } catch (Exception e) {
            logger.error("Order Silver Fact Job failed fatally!");

            throw new RuntimeException(e);
        } finally {
            if (spark != null) {
                spark.stop();
                logger.info("Spark Session Stopped.");
            }
        }
    }
}
