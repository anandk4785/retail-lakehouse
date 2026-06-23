package com.anand.retail.main;

import com.anand.retail.constants.LakehouseTable;
import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.reader.CustomerReader;
import com.anand.retail.service.CustomerService;
import com.anand.retail.writer.BronzeWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerBronzeJob {

    private static final Logger logger = LoggerFactory.getLogger(CustomerBronzeJob.class);

    public static void main(String[] args) {
        logger.info("Starting Customer Bronze Ingestion Job");

        SparkSession spark = SparkSessionFactory.getSparkSession();

        try {
            // 1. The "Assembly Line" wire-up (Dependency Injection)
            CustomerReader customerReader = new CustomerReader();
            CustomerService customerService = new CustomerService(customerReader);

            Dataset<Row> customerDf = customerService.loadAndShowCustomers(spark);

            // 2. The universal Enum write
            BronzeWriter bronzeWriter = new BronzeWriter();
            bronzeWriter.writeTable(customerDf, LakehouseTable.CUSTOMERS);

            logger.info("Customer Bronze Job Completed Successfully");

        } catch (Exception e) {
            logger.error("Customer Bronze Job failed fatally!", e);

            throw new RuntimeException(e);

        } finally {
            if (spark != null) {
                spark.stop();
                logger.info("Spark Session stopped.");
            }
        }
    }
}