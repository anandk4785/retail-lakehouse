package com.anand.retail.main;

import com.anand.retail.constants.LakehouseTable;
import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.reader.BronzeReader;
import com.anand.retail.transform.CustomerTransformer;
import com.anand.retail.writer.SilverWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerSilverJob {

    private static final Logger logger = LoggerFactory.getLogger(CustomerSilverJob.class);

    public static void main(String[] args) {
        logger.info("Starting Customer Silver Dimension Job");

        SparkSession spark = SparkSessionFactory.getSparkSession();

        try {
            // 1. Read from Bronze
            BronzeReader reader = new BronzeReader();
            Dataset<Row> bronzeDf = reader.readCustomers(spark);

            // 2. Transform (Service Layer)
            CustomerTransformer transformer = new CustomerTransformer();
            Dataset<Row> resultDf = transformer.transform(bronzeDf);

            // 3. Write to Silver
            SilverWriter silverWriter = new SilverWriter();
            silverWriter.writeTable(resultDf, LakehouseTable.CUSTOMERS);

            logger.info("Customer Silver Job Completed Successfully.");
        } catch (Exception e) {
            logger.error("Customer Silver Job failed fatally!", e);
            throw new RuntimeException(e);
        } finally {
            if (spark != null) {
                spark.stop();

                logger.info("Spark Session Stopped.");
            }
        }
    }
}
