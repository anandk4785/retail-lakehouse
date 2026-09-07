package com.anand.retail.service;

import com.anand.retail.config.ConfigLoader;
import com.anand.retail.constants.HiveTable;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Paths;

/**
 * Registers an already-written Parquet dataset as a Spark/Hive catalog
 * table, pointing at its existing on-disk location rather than moving or
 * copying data. Since the LOCATION is specified explicitly, the resulting
 * table behaves as external/unmanaged: dropping it from the catalog does
 * not delete the underlying Parquet files.
 */
public class HiveRegistrar implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(HiveRegistrar.class);

    public void register(SparkSession spark, HiveTable table) {
        String basePath = ConfigLoader.get(table.getHiveDatabase() + ".path");
        String tablePath = Paths.get(basePath, table.getHiveTableName()).toString();
        // Resolve to an absolute file:// URI to avoid relative-path
        // resolution issues in the LOCATION clause, same technique
        // HiveDatabaseService already uses for CREATE DATABASE.
        String locationUri = new File(tablePath).toURI().toString();

        String fullQualifiedName = table.getFullQualifiedName();

        logger.info("Registering Hive Table: [{}] at Location: [{}]",
                fullQualifiedName, locationUri);

        // Drop the table if it exists first. Since these are external tables
        // (defined with LOCATION), Spark's CREATE OR REPLACE TABLE is not supported.
        // Instead, we drop and recreate to make the registration idempotent.
        spark.sql("DROP TABLE IF EXISTS " + fullQualifiedName);

        spark.sql(
                "CREATE TABLE " + fullQualifiedName +
                        " USING PARQUET LOCATION '" + locationUri + "'"
        );

        logger.info("Registered Hive Table: [{}]", fullQualifiedName);
    }
}
