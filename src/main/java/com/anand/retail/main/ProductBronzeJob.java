package com.anand.retail.main;

import com.anand.retail.constants.LakehouseTable;
import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.reader.ProductReader;
import com.anand.retail.service.ProductService;
import com.anand.retail.writer.BronzeWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductBronzeJob {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    ProductBronzeJob.class);

    public static void main(String[] args) {

        logger.info("Starting Product Bronze Job");

        SparkSession spark = SparkSessionFactory.getSparkSession();

        try {
            ProductReader productReader = new ProductReader();
            ProductService productService = new ProductService(productReader);

            Dataset<Row> productDf = productService.loadAndShowProducts(spark);

            BronzeWriter bronzeWriter = new BronzeWriter();
            bronzeWriter.writeTable(productDf, LakehouseTable.PRODUCTS);

            logger.info("Product Bronze Job Completed Successfully");

        } catch (Exception e) {
            logger.error("Product Bronze Job failed fatally!", e);

            throw new RuntimeException(e);
        } finally {
            if (spark != null) {
                spark.stop();

                logger.info("Spark Session stopped");
            }
        }
    }
}
