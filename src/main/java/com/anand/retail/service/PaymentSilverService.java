package com.anand.retail.service;

import com.anand.retail.constants.LakehouseTable;
import com.anand.retail.reader.BronzeReader;
import com.anand.retail.transform.PaymentTransformer;
import com.anand.retail.validator.PaymentValidator;
import com.anand.retail.writer.SilverWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class PaymentSilverService implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(PaymentSilverService.class);

    private final BronzeReader reader;
    private final PaymentTransformer transformer;
    private final PaymentValidator validator;
    private final SilverWriter writer;

    public PaymentSilverService(
            BronzeReader reader,
            PaymentTransformer transformer,
            PaymentValidator validator,
            SilverWriter writer
    ) {
        this.reader = reader;
        this.transformer = transformer;
        this.validator = validator;
        this.writer = writer;
    }

    public void run(SparkSession spark) {
        logger.info("--- Starting Payment Silver Execution Flow ---");

        long startTime = System.currentTimeMillis();

        Dataset<Row> rawDf = reader.readTable(spark, LakehouseTable.PAYMENTS);
        long inputCount = rawDf.count();
        logger.info("Input Count: {}", inputCount);

        Dataset<Row> validDf = validator.validate(rawDf);
        long validCount = validDf.count();
        logger.info("Invalid/Duplicate Records Removed: {}", (inputCount - validCount));

        Dataset<Row> silverDf = transformer.transform(validDf);
        long outputCount = silverDf.count();
        logger.info("Output Records Count: {}", outputCount);

        writer.writeTable(silverDf, LakehouseTable.PAYMENTS);

        long endTime = System.currentTimeMillis();
        logger.info("--- Execution completed in {} ms", (endTime - startTime));
    }
}
