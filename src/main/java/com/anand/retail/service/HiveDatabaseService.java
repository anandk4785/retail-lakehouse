package com.anand.retail.service;

import com.anand.retail.config.ConfigLoader;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;

public class HiveDatabaseService implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(HiveDatabaseService.class);

    public void createLakehouseDatabases(SparkSession spark) {
        logger.info("--- Initializing Hive Metastore Databases ---");

        String[] databases = {"bronze", "silver", "gold"};

        for (String database : databases) {
            String pathConfig = ConfigLoader.get(database + ".path");
            // Resolve to absolute URI to prevent path resolution issues
            String locationUri = new File(pathConfig).toURI().toString();
            
            logger.info("Creating database if not exists: [{}] at LOCATION: [{}]", database, locationUri);
            spark.sql("CREATE DATABASE IF NOT EXISTS " + database + " LOCATION '" + locationUri + "'");
        }

        logger.info("--- Database Initialization Completed ---");
    }

    public void showDatabases(SparkSession spark) {
        logger.info("Current databases in Hive Metastore:");
        spark.sql("SHOW DATABASES").show(false);
    }

    public void describeDatabases(SparkSession spark) {
        String[] databases = {"bronze", "silver", "gold"};
        for (String database : databases) {
            logger.info("Describing database: [{}]", database);
            spark.sql("DESCRIBE DATABASE EXTENDED " + database).show(false);
        }
    }
}
