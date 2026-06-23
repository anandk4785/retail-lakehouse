package com.anand.retail.main;

import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.reader.CustomerReader;
import com.anand.retail.service.CustomerService;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloSparkJob {
    private static final Logger logger =
            LoggerFactory.getLogger(
                    HelloSparkJob.class
            );

    public static void main(
            String[] args
    ) {
        logger.info(
                "Starting Retail Lakehouse Hello Spark Job"
        );

        SparkSession spark = SparkSessionFactory.getSparkSession();

        try {
            // Wire up the dependencies since CustomerService is no longer static
            CustomerReader customerReader = new CustomerReader();
            CustomerService customerService = new CustomerService(customerReader);

            Dataset<Row> customerDf = customerService.loadAndShowCustomers(spark);

            logger.info("HelloSparkJob Completed Successfully");

        } catch (Exception e) {
            logger.error("HelloSparkJob failed!", e);
            throw new RuntimeException(e);
        } finally {
            if (spark != null) {
                spark.stop();
            }
        }
    }
}
