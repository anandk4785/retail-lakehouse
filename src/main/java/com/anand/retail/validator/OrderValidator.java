package com.anand.retail.validator;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderValidator {

    private static final Logger logger = LoggerFactory.getLogger(OrderValidator.class);

    public Dataset<Row> validate(Dataset<Row> df) {
        logger.info("Validating Order Data: Removing null PKs and duplicates");

        return df.filter(df.col("order_id").isNotNull())
                .filter(df.col("customer_id").isNotNull())
                .filter(df.col("order_purchase_timestamp").isNotNull())
                .dropDuplicates("order_id");
    }
}
