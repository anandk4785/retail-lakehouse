package com.anand.retail.helper;

import com.anand.retail.reader.CustomerReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;

public class CustomerJobHelper {

    /**
     * Loads customers using {@link CustomerReader}, prints schema and a sample,
     * logs the count and returns the dataset for further processing.
     */
    public static Dataset<Row> loadAndShowCustomers(SparkSession spark, Logger logger) {

        CustomerReader customerReader = new CustomerReader();

        Dataset<Row> customerDf = customerReader.readCustomers(spark);

        logger.info("Printing Schema");
        customerDf.printSchema();

        logger.info("Showing sample records");
        customerDf.show(10, false);

        long count = customerDf.count();
        logger.info("Customer Count : {}", count);

        return customerDf;
    }
}

