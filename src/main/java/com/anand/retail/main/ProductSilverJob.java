package com.anand.retail.main;

import com.anand.retail.constants.HiveTable;
import com.anand.retail.constants.LakehouseTable;
import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.reader.BronzeReader;
import com.anand.retail.schema.ProductSchema;
import com.anand.retail.service.HiveRegistrar;
import com.anand.retail.service.SilverService;
import com.anand.retail.transform.ProductTransformer;
import com.anand.retail.validator.NullPkDedupValidator;
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
            SilverService service = new SilverService(
                    new BronzeReader(),
                    new NullPkDedupValidator(
                            new String[]{ProductSchema.PRODUCT_ID},
                            new String[]{ProductSchema.PRODUCT_ID}
                    ),
                    new ProductTransformer(),
                    new SilverWriter(),
                    LakehouseTable.PRODUCTS
            );

            service.run(spark);

            // Register the freshly written Silver output as a queryable
            // Hive table now that the underlying Parquet data exists.
            new HiveRegistrar().register(spark, HiveTable.SILVER_PRODUCTS);

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