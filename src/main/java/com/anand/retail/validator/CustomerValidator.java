package com.anand.retail.validator;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerValidator {

    private static final Logger logger = LoggerFactory.getLogger(CustomerValidator.class);

    /**
     * Cleanses the data by removing null primary keys and duplicates.
     */
    public Dataset<Row> validate(Dataset<Row> df) {
        logger.info("Validating Customer Data: Removing null PKs and duplicates");

        return df
                .filter(df.col("customer_id").isNotNull())
                .dropDuplicates("customer_id");
    }
}
