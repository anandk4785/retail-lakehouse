package com.anand.retail.transform;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class ProductTransformer implements DataTransformer, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ProductTransformer.class);

    @Override
    public Dataset<Row> transform(Dataset<Row> df) {
        logger.info("Applying Silver transformations for Product Dimension");

        return df.na().fill("Unknown", new String[]{"product_category_name"});
    }
}
