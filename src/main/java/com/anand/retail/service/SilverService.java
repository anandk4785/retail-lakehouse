package com.anand.retail.service;

import com.anand.retail.constants.LakehouseTable;
import com.anand.retail.reader.BronzeReader;
import com.anand.retail.transform.DataTransformer;
import com.anand.retail.validator.DataValidator;
import com.anand.retail.writer.SilverWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class SilverService implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(SilverService.class);

    private final BronzeReader reader;
    private final DataValidator validator;
    private final DataTransformer transformer;
    private final SilverWriter writer;
    private final LakehouseTable table;

    public SilverService(
            BronzeReader reader,
            DataValidator validator,
            DataTransformer transformer,
            SilverWriter writer,
            LakehouseTable table
    ) {
        this.reader = reader;
        this.validator = validator;
        this.transformer = transformer;
        this.writer = writer;
        this.table = table;
    }

    public void run(SparkSession spark) {
        logger.info("--- Starting {} Silver Execution Flow ---", table.name());
        long startTime = System.currentTimeMillis();

        Dataset<Row> rawDf = reader.readTable(spark, table);
        long inputCount = rawDf.count();
        logger.info("Input Count: {}", inputCount);

        Dataset<Row> validDf = validator.validate(rawDf);
        long validCount = validDf.count();
        logger.info("Invalid/Duplicate records removed: {}", (inputCount - validCount));

        Dataset<Row> silverDf = transformer.transform(validDf);
        long outputCount = silverDf.count();
        logger.info("Output records count: {}", outputCount);

        writer.writeTable(silverDf, table);

        long endTime = System.currentTimeMillis();
        logger.info("--- Execution Completed in {} ms ---", (endTime - startTime));
    }
}
