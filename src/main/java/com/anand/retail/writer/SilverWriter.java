package com.anand.retail.writer;

import com.anand.retail.config.ConfigLoader;
import com.anand.retail.constants.LakehouseTable;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class SilverWriter {
    private static final Logger logger = LoggerFactory.getLogger(SilverWriter.class);

    public void writeTable(Dataset<Row> df, LakehouseTable table) {

        String silverPath = ConfigLoader.get("silver.path");
        String destinationPath = Paths.get(silverPath, table.getDirectoryName()).toString();

        logger.info("Writing Silver table [{}] to path: {}", table.name(), destinationPath);

        df.write()
                .mode(SaveMode.Overwrite)
                .parquet(destinationPath);
    }
}
