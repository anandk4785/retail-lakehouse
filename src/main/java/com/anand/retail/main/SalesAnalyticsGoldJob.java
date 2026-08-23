package com.anand.retail.main;

import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.service.SalesAnalyticsService;
import com.anand.retail.writer.HiveWriter;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SalesAnalyticsGoldJob {

    private static final Logger logger = LoggerFactory.getLogger(SalesAnalyticsGoldJob.class);

    private SalesAnalyticsGoldJob() {

    }

    public static void main(String[] args) {
        logger.info("Starting Sales Analytics Gold Job");

        SparkSession spark = SparkSessionFactory.getSparkSession();

        try {
            SalesAnalyticsService service = new SalesAnalyticsService(
                    new HiveWriter(),
                    "gold",
                    "monthly_product_sales"
            );

            service.run(spark);

            logger.info("Sales Analytics Gold Job completed successfully");
        } catch (Exception e) {
            logger.error("Sales Analytics Gold Job failed fatally!", e);
            throw new RuntimeException(e);
        } finally {
            if (spark != null) {
                spark.stop();
                logger.info("Spark Session Stopped.");
            }
        }
    }
}