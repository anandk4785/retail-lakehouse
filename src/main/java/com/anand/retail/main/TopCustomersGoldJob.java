package com.anand.retail.main;

import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.service.TopCustomersService;
import com.anand.retail.writer.HiveWriter;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopCustomersGoldJob {

    private static final Logger logger = LoggerFactory.getLogger(TopCustomersGoldJob.class);

    private TopCustomersGoldJob() {
        // Private constructor to prevent instantiation
    }

    public static void main(String[] args) {
        logger.info("Starting Top Customers Gold Job");

        SparkSession spark = SparkSessionFactory.getSparkSession();

        try {
            TopCustomersService service = new TopCustomersService(
                    new HiveWriter(),
                    "gold",
                    "top_customers"
            );

            service.run(spark);

            logger.info("Top Customers Gold Job Completed Successfully.");
        } catch (Exception e) {
            logger.error("Top Customers Gold Job Failed fatally!", e);
            throw new RuntimeException(e);
        } finally {
            if (spark != null) {
                spark.stop();
                logger.info("Spark Session Stopped.");
            }
        }
    }
}
