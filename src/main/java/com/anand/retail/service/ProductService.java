package com.anand.retail.service;

import com.anand.retail.reader.ProductReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class ProductService implements Serializable {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    ProductService.class);

    private final ProductReader productReader;

    public ProductService(ProductReader productReader) {
        this.productReader = productReader;
    }

    public Dataset<Row> loadAndShowProducts(SparkSession spark) {

        Dataset<Row> productDf = productReader.readProducts(spark);

        logger.info("Printing schema for products");
        productDf.printSchema();

        logger.info("Showing sample product records");
        productDf.show(10, false);

        long count = productDf.count();
        logger.info("Product count : {}", count);

        return productDf;
    }
}
