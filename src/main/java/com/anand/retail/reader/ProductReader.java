package com.anand.retail.reader;

import com.anand.retail.config.ConfigLoader;
import com.anand.retail.constants.DatasetConstants;
import com.anand.retail.schema.ProductSchema;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class ProductReader {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    ProductReader.class);


    public Dataset<Row> readProducts(SparkSession spark) {

        logger.info("Reading product dataset");
        String datasetRoot = ConfigLoader.get("dataset.root");
        String productFile = ConfigLoader.get(DatasetConstants.PRODUCTS);
        String fullPath = Paths.get(datasetRoot, productFile).toString();
        logger.info("Product dataset path : {}", fullPath);

        return spark
                .read()
                .option("header", true)
                .schema(ProductSchema.getSchema())
                .csv(fullPath);
    }
}
