package com.anand.retail.service;

import com.anand.retail.constants.LakehouseTable;
import com.anand.retail.reader.BronzeReader;
import com.anand.retail.transform.OrderTransformer;
import com.anand.retail.validator.OrderValidator;
import com.anand.retail.writer.SilverWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class OrderSilverService implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(OrderSilverService.class);

    private final BronzeReader reader;
    private final OrderTransformer transformer;
    private final OrderValidator validator;
    private final SilverWriter writer;

    public OrderSilverService(
            BronzeReader reader,
            OrderTransformer transformer,
            OrderValidator validator,
            SilverWriter writer
    ) {
        this.reader = reader;
        this.transformer = transformer;
        this.validator = validator;
        this.writer = writer;
    }

    public void run(SparkSession spark) {
        logger.info("--- Starting Order Silver Execution Flow ---");

        long startTime = System.currentTimeMillis();

        Dataset<Row> rawDf = reader.readTable(spark, LakehouseTable.ORDERS);
        long inputCount = rawDf.count();
        logger.info("Input Count: {}", inputCount);

        Dataset<Row> validDf = validator.validate(rawDf);
        long validCount = validDf.count();
        logger.info("Invalid/Duplicate Records Removed: {}", (inputCount - validCount));

        Dataset<Row> silverDf = transformer.transform(validDf);
        long outputCount = silverDf.count();
        logger.info("Output Records Count: {}", outputCount);

        writer.writeTable(silverDf, LakehouseTable.ORDERS);

        long endTime = System.currentTimeMillis();
        logger.info("--- Execution completed in {} ms", (endTime - startTime));
    }
}
