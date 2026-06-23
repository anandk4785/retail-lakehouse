package com.anand.retail.service;

import com.anand.retail.reader.CustomerReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

// 1. Implements Serializable to protect against Spark network serialization leaks
public class CustomerService implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerReader customerReader;

    // 2. True Dependency Injection: We demand the Reader, we don't 'new' it up
    public CustomerService(CustomerReader customerReader) {
        this.customerReader = customerReader;
    }

    /**
     * Loads customers using the injected {@link CustomerReader}, prints schema and a sample,
     * logs the count and returns the dataset for further processing.
     */
    public Dataset<Row> loadAndShowCustomers(SparkSession spark) {

        Dataset<Row> customerDf = customerReader.readCustomers(spark);

        logger.info("Printing Schema for Customers");
        customerDf.printSchema();

        logger.info("Showing sample customer records");
        customerDf.show(10, false);

        long count = customerDf.count();
        logger.info("Customer Count : {}", count);

        return customerDf;
    }
}