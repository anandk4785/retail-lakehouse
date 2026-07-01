package com.anand.retail.transform;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.upper;
import static org.apache.spark.sql.functions.trim;
import static org.apache.spark.sql.functions.initcap;



public class CustomerTransformer {

    private static final Logger logger = LoggerFactory.getLogger(CustomerTransformer.class);

    /**
     * Transforms raw Bronze customer data into a clean Silver Dimension.
     */
    public Dataset<Row> transform(Dataset<Row> customerDf) {
        logger.info("Applying Silver transformations for Customer Dimension");

        return customerDf
                .withColumn("customer_state", upper(trim(col("customer_state"))))
                .withColumn("customer_city", initcap(trim(col("customer_city"))));
    }
}
