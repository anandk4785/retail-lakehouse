package com.anand.retail.reader;

import com.anand.retail.config.ConfigLoader;
import com.anand.retail.constants.DatasetConstants;
import com.anand.retail.schema.OrderItemSchema;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class OrderItemReader {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemReader.class);

    public Dataset<Row> readOrderItem(SparkSession spark) {
        logger.info("Reading order items dataset");
        String datasetRoot = ConfigLoader.get("dataset.root");
        String orderItemFile = ConfigLoader.get(DatasetConstants.ORDER_ITEMS);
        String fullPath = Paths.get(datasetRoot, orderItemFile).toString();
        logger.info("Order items dataset path : {}", fullPath);

        return spark
                .read()
                .option("header", true)
                .schema(OrderItemSchema.getSchema())
                .csv(fullPath);
    }
}
