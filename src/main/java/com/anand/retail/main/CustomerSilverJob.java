package com.anand.retail.main;

import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.reader.BronzeReader;
import com.anand.retail.service.CustomerSilverService;
import com.anand.retail.transform.CustomerTransformer;
import com.anand.retail.validator.CustomerValidator;
import com.anand.retail.writer.SilverWriter;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerSilverJob {

    private static final Logger logger = LoggerFactory.getLogger(CustomerSilverJob.class);

    public static void main(String[] args) {
        logger.info("Starting Customer Silver Dimension Job");

        SparkSession spark = SparkSessionFactory.getSparkSession();

        try {
            // Assembly Line: Injecting Dependenciesze
            CustomerSilverService service = new CustomerSilverService(
                    new BronzeReader(),
                    new CustomerTransformer(),
                    new CustomerValidator(),
                    new SilverWriter()
            );

            // Execute the pipeline
            service.run(spark);

            logger.info("Customer Silver Job Completed Successfully.");
        } catch (Exception e) {
            logger.error("Customer Silver Job failed fatally!", e);
            //System.exit(1); // Standard OS exit code to signal orchestrators (like Airflow) that the job failed
            throw new RuntimeException(e);
        } finally {
            if (spark != null) {
                spark.stop();

                logger.info("Spark Session Stopped.");
            }
        }
    }
}
