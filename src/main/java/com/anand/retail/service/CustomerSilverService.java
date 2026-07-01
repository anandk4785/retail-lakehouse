package com.anand.retail.service;

import com.anand.retail.constants.LakehouseTable;
import com.anand.retail.reader.BronzeReader;
import com.anand.retail.transform.CustomerTransformer;
import com.anand.retail.validator.CustomerValidator;
import com.anand.retail.writer.SilverWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class CustomerSilverService implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(CustomerSilverService.class);

    private final BronzeReader reader;
    private final CustomerTransformer transformer;
    private final CustomerValidator validator;
    private final SilverWriter writer;

    public CustomerSilverService(
            BronzeReader reader,
            CustomerTransformer transformer,
            CustomerValidator validator,
            SilverWriter writer) {
        this.reader = reader;
        this.transformer = transformer;
        this.validator = validator;
        this.writer = writer;
    }

    public void run(SparkSession spark) {
        logger.info("--- Starting Customer Silver Execution Flow ---");

        long startTime = System.currentTimeMillis();

        // 1. Read from Bronze
        Dataset<Row> rawDf = reader.readTable(spark, LakehouseTable.CUSTOMERS);
        long inputCount = rawDf.count();
        logger.info("Input Count: {}", inputCount);

        // 2. Validate
        Dataset<Row> validDf = validator.validate(rawDf);
        long validCount = validDf.count();
        logger.info("Invalid/Duplicate Records Removed: {}", (inputCount - validCount));

        // 3. Transform
        Dataset<Row> silverDf = transformer.transform(validDf);
        long outputCount = silverDf.count();
        logger.info("Output Records Count: {}", outputCount);

        // 4. Write to Silver
        writer.writeTable(silverDf, LakehouseTable.CUSTOMERS);

        long endTime = System.currentTimeMillis();
        logger.info("--- Execution Completed in {} ms ---", (endTime - startTime));
    }
}
