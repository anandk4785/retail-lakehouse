package com.anand.retail.service;

import com.anand.retail.constants.LakehouseTable;
import com.anand.retail.reader.BronzeReader;
import com.anand.retail.transform.ProductTransformer;
import com.anand.retail.validator.ProductValidator;
import com.anand.retail.writer.SilverWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductSilverService {

    private static final Logger logger = LoggerFactory.getLogger(ProductSilverService.class);

    private final BronzeReader reader;
    private final ProductTransformer transformer;
    private final ProductValidator validator;
    private final SilverWriter writer;

    public ProductSilverService(
            BronzeReader reader,
            ProductTransformer transformer,
            ProductValidator validator,
            SilverWriter writer) {
        this.reader = reader;
        this.transformer = transformer;
        this.validator = validator;
        this.writer = writer;
    }

    public void run(SparkSession spark) {
        logger.info("--- Starting Product Silver Execution Flow ---");
        long startTime = System.currentTimeMillis();

        Dataset<Row> rawDf = reader.readTable(spark, LakehouseTable.PRODUCTS);
        long inputCount = rawDf.count();
        logger.info("Input Count: {}", inputCount);

        Dataset<Row> valiDf = validator.validate(rawDf);
        long validCount = valiDf.count();
        logger.info("Invalid/Duplicate records removed: {}", (inputCount - validCount));

        Dataset<Row> silverDf = transformer.transform(valiDf);
        long outputCount = silverDf.count();
        logger.info("Output records count: {}", outputCount);

        writer.writeTable(silverDf, LakehouseTable.PRODUCTS);

        long endTime = System.currentTimeMillis();
        logger.info("--- Execution Completed in {} ms ---", (endTime - startTime));
    }
}
