package com.anand.retail.main;

import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.helper.CustomerJobHelper;
import com.anand.retail.writer.BronzeWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerBronzeJob {

    private static final Logger logger =
            LoggerFactory.getLogger(CustomerBronzeJob.class);

    public static void main(String[] args) {
        logger.info("Starting Customer Bronze Job");

        SparkSession spark =
                SparkSessionFactory.getSparkSession();
        try {
            Dataset<Row> customerDf = CustomerJobHelper.loadAndShowCustomers(spark, logger);

            BronzeWriter bronzeWriter = new BronzeWriter();
            bronzeWriter.writeCustomers(customerDf);

            logger.info("Job Completed Successfully");
        } catch (Exception e) {
            logger.error("Customer Bronze Job failed", e);
        } finally {
            if (spark != null) {
                spark.stop();
            }
        }


    }
}
