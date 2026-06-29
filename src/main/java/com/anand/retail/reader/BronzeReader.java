package com.anand.retail.reader;

import com.anand.retail.config.ConfigLoader;
import com.anand.retail.constants.LakehouseTable;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class BronzeReader {

    private static final Logger logger = LoggerFactory.getLogger(BronzeReader.class);

    public Dataset<Row> readCustomers(SparkSession spark) {
        logger.info("Reading customer dataset from bronze layer.");

        String bronzeRoot = ConfigLoader.get("bronze.path");
        // Pass the directory name (String) instead of the enum constant itself.
        // Using getDirectoryName() returns the intended "customers" value
        // (String.valueOf(LakehouseTable.CUSTOMERS) would yield "CUSTOMERS").
        String fullPath = Paths.get(bronzeRoot, LakehouseTable.CUSTOMERS.getDirectoryName()).toString();

        return spark.read().parquet(fullPath);
    }
}
