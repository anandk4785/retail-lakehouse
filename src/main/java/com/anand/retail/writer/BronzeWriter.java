package com.anand.retail.writer;

import com.anand.retail.config.ConfigLoader;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class BronzeWriter {
    private static final Logger logger =
            LoggerFactory.getLogger(
                    BronzeWriter.class
            );

    public void writeCustomers(Dataset<Row> customerDf) {
        String bronzePath = ConfigLoader.get("bronze.path");
        String customerBronzePath = Paths.get(bronzePath, "customers").toString();
        logger.info("Writing customers to Bronze : {}", customerBronzePath);
        customerDf.write()
                .mode(SaveMode.Overwrite)
                .parquet(customerBronzePath);
    }
}
