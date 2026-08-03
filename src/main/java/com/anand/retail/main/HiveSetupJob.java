package com.anand.retail.main;

import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.service.HiveDatabaseService;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HiveSetupJob {
    private static final Logger logger = LoggerFactory.getLogger(HiveSetupJob.class);

    public static void main(String[] args) {
        logger.info("Starting Hive Setup Job");

        SparkSession spark = SparkSessionFactory.getSparkSession();

        try {
            HiveDatabaseService service = new HiveDatabaseService();

            service.createLakehouseDatabases(spark);

            service.showDatabases(spark);

            service.describeDatabases(spark);

            logger.info("Hive Metastore Setup Completed Successfully");
        } catch (Exception e) {
            logger.error("Hive Metastore Setup failed fatally!", e);

            throw new RuntimeException(e);
        } finally {
            if (spark != null) {
                spark.stop();
                logger.info("Spark Session Stopped.");
            }
        }
    }
}
