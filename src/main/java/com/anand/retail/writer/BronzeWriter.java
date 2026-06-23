package com.anand.retail.writer;

import com.anand.retail.config.ConfigLoader;

import com.anand.retail.constants.LakehouseTable;
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

    /**
     * A single, universal method to write any Bronze table safely.
     */
    public void writeTable(Dataset<Row> df, LakehouseTable table) {
        String bronzePath = ConfigLoader.get("bronze.path");
        String destinationPath = Paths.get(bronzePath, table.getDirectoryName()).toString();

        logger.info("Writing {} to Bronze layer at: {}", table.name(), destinationPath);

        df.write()
                .mode(SaveMode.Overwrite)
                .parquet(destinationPath);
    }
}
