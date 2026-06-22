package com.anand.retail.reader;

import com.anand.retail.config.ConfigLoader;
import com.anand.retail.constants.DatasetConstants;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class CustomerReader {

    private static final Logger logger =

            LoggerFactory.getLogger(
                    CustomerReader.class
            );

    public Dataset<Row> readCustomers(
            SparkSession spark
    ) {
        logger.info("Reading customer dataset");
        String datasetRoot = ConfigLoader.get("dataset.root");
        String customerFile = ConfigLoader.get(DatasetConstants.CUSTOMERS);
        String fullPath = Paths.get(datasetRoot, customerFile).toString();
        logger.info("Customer dataset path : {}", fullPath);

        return spark
                .read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv(fullPath);
    }
}
