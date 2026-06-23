package com.anand.retail.main;

import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.helper.CustomerJobHelper;

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
                "Starting Retail Lakehouse Job"
        );

        SparkSession spark = SparkSessionFactory.getSparkSession();

        Dataset<Row> customerDf = CustomerJobHelper.loadAndShowCustomers(spark, logger);

        spark.stop();

        logger.info("Job Completed Successfully");
    }
}
