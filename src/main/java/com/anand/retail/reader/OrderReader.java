package com.anand.retail.reader;

import com.anand.retail.config.ConfigLoader;
import com.anand.retail.constants.DatasetConstants;
import com.anand.retail.schema.OrderSchema;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class OrderReader {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    OrderReader.class);

    public Dataset<Row> readOrders(SparkSession spark) {
        logger.info("Reading orders dataset");
        String datasetRoot = ConfigLoader.get("dataset.root");
        String orderFile = ConfigLoader.get(DatasetConstants.ORDERS);
        String fullPath = Paths.get(datasetRoot, orderFile).toString();
        logger.info("Orders dataset path : {}", fullPath);

        return spark
                .read()
                .option("header", true)
                .schema(OrderSchema.getSchema())
                .csv(fullPath);
    }
}
