package com.anand.retail.writer;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Writes Gold datasets as Spark-managed Hive tables. Unlike HiveRegistrar,
 * this writer intentionally supplies no LOCATION: Spark owns the table
 * data beneath the configured warehouse directory.
 *
 * Deliberately does NOT depend on HiveTable/LakehouseTable. Those exist
 * to identify already-ingested, directory-backed entities (Bronze/Silver
 * tables); a Gold aggregate like monthly_product_sales has no such
 * physical dataset behind it — it's derived, not ingested. Forcing it
 * through HiveTable would mean inventing a LakehouseTable entry with no
 * real directory to resolve. See ADR-021's "Rejected Direction" for the
 * same reasoning applied when HiveWriter was first deferred.
 */
public class HiveWriter implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(HiveWriter.class);

    public void writeManagedTable(
            Dataset<Row> df,
            String database,
            String tableName,
            SaveMode mode
    ) {
        String fullyQualifiedName = database + "." + tableName;

        logger.info("Writing managed Hive table [{}] with mode [{}]", fullyQualifiedName, mode);

        df.write()
                .format("parquet")
                .mode(mode)
                .saveAsTable(fullyQualifiedName);

        logger.info("Managed Hive table written successfully: [{}]", fullyQualifiedName);
    }
}