package com.anand.retail.main;

import com.anand.retail.factory.SparkSessionFactory;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ad-hoc diagnostic job: runs through the project's own
 * SparkSessionFactory (same config as every other job) to verify the
 * Hive Metastore databases and tables, rather than relying on a
 * separately-configured spark-shell/spark-sql session that would need
 * every config flag matched by hand.
 *
 * Checks all five registered Silver tables (row counts as a quick
 * sanity check, plus one detailed schema/sample check on
 * silver.customers as a representative example), and the real Gold
 * output written by SalesAnalyticsGoldJob.
 *
 * PREREQUISITE: assumes HiveSetupJob has been run at least once (so the
 * silver/gold databases exist), every *SilverJob has been run at least
 * once (so silver.* tables are registered), and SalesAnalyticsGoldJob
 * has been run at least once (so gold.monthly_product_sales exists) —
 * otherwise the corresponding SELECT/DESCRIBE calls below will fail
 * with a "table not found" error.
 *
 * NOTE: this job no longer writes a throwaway demo table
 * (gold.hive_writer_verification) the way it did back in US-014, before
 * a real Gold job existed to verify against. That old demo table is
 * safe to drop manually from your local warehouse
 * (DROP TABLE gold.hive_writer_verification) — it's not referenced or
 * recreated by anything anymore.
 */
public class HiveVerificationJob {

    private static final Logger logger = LoggerFactory.getLogger(HiveVerificationJob.class);

    private static final String[] SILVER_TABLES = {
            "customers", "products", "orders", "payments", "order_items"
    };

    public static void main(String[] args) {
        logger.info("Starting Hive Verification Job");

        SparkSession spark = SparkSessionFactory.getSparkSession();

        try {
            // --- Databases ---
            spark.sql("SHOW DATABASES").show(false);

            // --- Silver: external tables registered via HiveRegistrar ---
            spark.sql("SHOW TABLES IN silver").show(false);

            for (String table : SILVER_TABLES) {
                long count = spark.sql("SELECT * FROM silver." + table).count();
                logger.info("silver.{} row count: {}", table, count);
            }

            // Representative detailed check on one Silver table
            spark.sql("DESCRIBE TABLE EXTENDED silver.customers").show(false);
            spark.sql("SELECT * FROM silver.customers LIMIT 5").show(false);

            // --- Gold: managed table written by SalesAnalyticsGoldJob ---
            spark.sql("SHOW TABLES IN gold").show(false);
            spark.sql("DESCRIBE TABLE EXTENDED gold.monthly_product_sales").show(false);
            spark.sql(
                    "SELECT * FROM gold.monthly_product_sales " +
                            "ORDER BY purchase_year, purchase_month LIMIT 10"
            ).show(false);

            logger.info("Hive Verification Completed Successfully.");
        } catch (Exception e) {
            logger.error("Hive Verification failed!", e);
            throw new RuntimeException(e);
        } finally {
            if (spark != null) {
                spark.stop();
                logger.info("Spark Session Stopped.");
            }
        }
    }
}