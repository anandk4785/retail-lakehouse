package com.anand.retail.factory;

import com.anand.retail.config.ConfigLoader;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SparkSessionFactory {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    SparkSessionFactory.class);

    private static SparkSession sparkSession;

    private SparkSessionFactory() {

    }

    public static SparkSession getSparkSession() {

        if (sparkSession == null) {

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

                        ConfigLoader.get(

                                "spark.sql.warehouse.dir"
                        )
                )

                //.enableHiveSupport()  <-- Commented out until Sprint 4

                .getOrCreate();

    }

}