package com.anand.retail.main;

import com.anand.retail.constants.HiveTable;
import com.anand.retail.constants.LakehouseTable;
import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.reader.BronzeReader;
import com.anand.retail.schema.OrderSchema;
import com.anand.retail.service.HiveRegistrar;
import com.anand.retail.service.SilverService;
import com.anand.retail.transform.OrderTransformer;
import com.anand.retail.validator.NullPkDedupValidator;
import com.anand.retail.writer.SilverWriter;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderSilverJob {
    private static final Logger logger = LoggerFactory.getLogger(OrderSilverJob.class);

    public static void main(String[] args) {
        logger.info("Starting Order Silver Fact Job");

        SparkSession spark = SparkSessionFactory.getSparkSession();

        try {

            SilverService service = new SilverService(
                    new BronzeReader(),
                    new NullPkDedupValidator(
                            new String[]{
                                    OrderSchema.ORDER_ID,
                                    OrderSchema.CUSTOMER_ID,
                                    OrderSchema.ORDER_PURCHASE_TIMESTAMP
                            },
                            new String[]{OrderSchema.ORDER_ID}
                    ),
                    new OrderTransformer(),
                    new SilverWriter(),
                    LakehouseTable.ORDERS
            );

            service.run(spark);

            // Register the freshly written Silver output as a queryable
            // Hive table now that the underlying Parquet data exists.
            new HiveRegistrar().register(spark, HiveTable.SILVER_ORDERS);

            logger.info("Order Silver Fact Job Completed successfully.");
        } catch (Exception e) {
            logger.error("Order Silver Fact Job failed fatally!", e);

            throw new RuntimeException(e);
        } finally {
            if (spark != null) {
                spark.stop();
                logger.info("Spark Session Stopped.");
            }
        }
    }
}