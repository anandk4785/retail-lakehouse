package com.anand.retail.validator;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductValidator {

    private static final Logger logger = LoggerFactory.getLogger(ProductValidator.class);

    public Dataset<Row> validate(Dataset<Row> df) {
        logger.info("Validating Product Data: Removing null PKs and duplicates");

        return df
                .filter(df.col("product_id").isNotNull())
                .dropDuplicates("product_id");
    }
}