package com.anand.retail.transform;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class OrderItemTransformer implements DataTransformer, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemTransformer.class);

    @Override
    public Dataset<Row> transform(Dataset<Row> df) {
        // Minimal transformation for Silver layer. Business metrics (like
        // total_item_value) belong in Gold. Silver stays close to
        // standardized source data.
        logger.info("No Silver transformations required for Order Items — returning standardized Bronze data as-is");
        return df;
    }
}