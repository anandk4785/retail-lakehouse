package com.anand.retail.main;

import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.reader.BronzeReader;
import com.anand.retail.service.ProductSilverService;
import com.anand.retail.transform.ProductTransformer;
import com.anand.retail.validator.ProductValidator;
import com.anand.retail.writer.SilverWriter;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductSilverJob {

    private static final Logger logger = LoggerFactory.getLogger(ProductSilverJob.class);

    public static void main(String[] args) {
        logger.info("Starting Product Silver Dimension Job");

        SparkSession spark = SparkSessionFactory.getSparkSession();

        try {
            ProductSilverService service = new ProductSilverService(
                    new BronzeReader(),
                    new ProductTransformer(),
                    new ProductValidator(),
                    new SilverWriter()
            );

            service.run(spark);

            logger.info("Product Silver Dimension Job Completed Successfully.");
        } catch (Exception e) {
            logger.error("Product Silver Dimension Job failed fatally!", e);
            throw new RuntimeException(e);
        } finally {
            if (spark != null) {
                spark.stop();
                logger.info("Spark Session Stopped.");
            }
        }
    }
}
