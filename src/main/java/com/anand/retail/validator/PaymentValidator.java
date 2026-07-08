package com.anand.retail.validator;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentValidator {

    private static final Logger logger = LoggerFactory.getLogger(PaymentValidator.class);

    public Dataset<Row> validate(Dataset<Row> df) {
        logger.info("Validating Payment Data: Removing null keys, " +
                "non-positive amounts, and duplicates on composite key (order_id, payment_sequential)");
        return df
                .filter(df.col("order_id").isNotNull())
                .filter(df.col("payment_sequential").isNotNull())
                .filter(df.col("payment_type").isNotNull())
                .filter(df.col("payment_value").geq(0))
                .dropDuplicates("order_id", "payment_sequential");
    }
}
