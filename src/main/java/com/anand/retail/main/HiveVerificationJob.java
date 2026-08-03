package com.anand.retail.main;

import com.anand.retail.factory.SparkSessionFactory;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ad-hoc diagnostic job: runs through the project's own
 * SparkSessionFactory (same config as every other job) to verify the
 * Hive Metastore databases and registered tables, rather than relying
 * on a separately-configured spark-shell/spark-sql session that would
 * need every config flag matched by hand.
 */
public class HiveVerificationJob {

    private static final Logger logger = LoggerFactory.getLogger(HiveVerificationJob.class);

    public static void main(String[] args) {
        logger.info("Starting Hive Verification Job");

        SparkSession spark = SparkSessionFactory.getSparkSession();

        try {
            spark.sql("SHOW DATABASES").show(false);
            spark.sql("SHOW TABLES IN SILVER").show(false);
            spark.sql("DESCRIBE TABLE EXTENDED silver.payments").show(false);
            spark.sql("SELECT * FROM silver.payments LIMIT 5").show(false);

            logger.info("Hive Verification Completed Successfully");
        } catch (Exception e) {
            logger.error("Hive Verification Failed!", e);
            throw new RuntimeException(e);
        } finally {
            if (spark != null) {
                spark.stop();
                logger.info("Spark Session Stopped.");
            }
        }
    }
}
