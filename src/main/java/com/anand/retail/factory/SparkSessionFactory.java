package com.anand.retail.factory;

import com.anand.retail.config.ConfigLoader;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public final class SparkSessionFactory {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    SparkSessionFactory.class);

    private static SparkSession sparkSession;

    private SparkSessionFactory() {

    }

    public static SparkSession getSparkSession() {

        if (sparkSession == null || sparkSession.sparkContext().isStopped()) {

            logger.info(
                    "Creating SparkSession");

            sparkSession =
                    createSparkSession();

            logger.info(
                    "SparkSession created successfully");

        }

        else {

            logger.info(
                    "Using existing SparkSession");

        }

        return sparkSession;

    }


    private static SparkSession createSparkSession() {

        // Derby writes its own log file relative to the JVM's working
        // directory by default (independent of javax.jdo.option.ConnectionURL
        // below). Relocate it under logs/, and ensure that directory exists
        // first — Derby does not reliably create missing parent directories
        // for its log file.
        File logsDir = new File("./logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }
        System.setProperty(
                "derby.stream.error.file",
                "./logs/derby.log"
        );

        // The embedded Derby metastore location is a SEPARATE setting from
        // spark.sql.warehouse.dir: warehouse.dir controls where managed
        // TABLE DATA lands; javax.jdo.option.ConnectionURL controls where
        // the metastore CATALOG itself (metastore_db/) lands. Left unset,
        // Derby defaults to a relative path resolved from the JVM's working
        // directory, ignoring warehouse.dir entirely — that would violate
        // ADR-008 (centralized storage under data/<env>/) the same way the
        // pre-ADR-008 default spark-warehouse/ did.
        String warehouseDir = ConfigLoader.get("spark.sql.warehouse.dir");
        String metastorePath = ConfigLoader.get("hive.metastore.path");

        return SparkSession

                .builder()

                .appName(

                        ConfigLoader.get(
                                "app.name"
                        )
                )

                .master(

                        ConfigLoader.get(
                                "spark.master"
                        )
                )

                .config(

                        "spark.sql.shuffle.partitions",

                        ConfigLoader.get(

                                "spark.sql.shuffle.partitions"
                        )
                )

                .config(
                        "spark.sql.warehouse.dir",
                        warehouseDir
                )

                .config(
                        "javax.jdo.option.ConnectionURL",
                        "jdbc:derby:;databaseName=" + metastorePath + ";create=true"
                )

                // Not environment-specific — always required for a fresh
                // embedded Derby metastore, so kept as a literal rather than
                // routed through application.properties.
                .config(
                        "datanucleus.schema.autoCreateAll",
                        "true"
                )

                // Redundant with enableHiveSupport() below (which already
                // sets this internally) — kept explicit for self-documentation.
                .config(
                        "spark.sql.catalogImplementation",
                        "hive"
                )

                .enableHiveSupport()

                .getOrCreate();

    }

}