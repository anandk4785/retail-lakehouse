package com.anand.retail.reader;

import com.anand.retail.config.ConfigLoader;
import com.anand.retail.constants.DatasetConstants;
import com.anand.retail.schema.PaymentSchema;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class PaymentReader {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    PaymentReader.class);

    public Dataset<Row> readPayments(SparkSession spark) {
        logger.info("Reading payments dataset");
        String datasetRoot = ConfigLoader.get("dataset.root");
        String paymentFile = ConfigLoader.get(DatasetConstants.PAYMENTS);
        String fullPath = Paths.get(datasetRoot, paymentFile).toString();
        logger.info("Payments dataset path : {}", fullPath);

        return spark
                .read()
                .option("header", true)
                .schema(PaymentSchema.getSchema())
                .csv(fullPath);
    }
}
