package com.anand.retail.main;

import com.anand.retail.constants.HiveTable;
import com.anand.retail.constants.LakehouseTable;
import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.reader.BronzeReader;
import com.anand.retail.schema.OrderItemSchema;
import com.anand.retail.service.HiveRegistrar;
import com.anand.retail.service.SilverService;
import com.anand.retail.transform.OrderItemTransformer;
import com.anand.retail.validator.NullPkDedupValidator;
import com.anand.retail.writer.SilverWriter;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderItemSilverJob {
    private static final Logger logger = LoggerFactory.getLogger(OrderItemSilverJob.class);

    public static void main(String[] args) {
        logger.info("Starting OrderItemSilverJob");

        SparkSession spark = SparkSessionFactory.getSparkSession();

        try {
            BronzeReader reader = new BronzeReader();
            NullPkDedupValidator validator = new NullPkDedupValidator(
                    new String[]{
                            OrderItemSchema.ORDER_ID,
                            OrderItemSchema.ORDER_ITEM_ID,
                            OrderItemSchema.PRODUCT_ID
                    },
                    new String[]{
                            OrderItemSchema.ORDER_ID,
                            OrderItemSchema.ORDER_ITEM_ID
                    }
            );
            OrderItemTransformer transformer = new OrderItemTransformer();
            SilverWriter writer = new SilverWriter();

            SilverService service = new SilverService(
                    reader,
                    validator,
                    transformer,
                    writer,
                    LakehouseTable.ORDER_ITEMS
            );

            service.run(spark);
            
            HiveRegistrar registrar = new HiveRegistrar();
            registrar.register(spark, HiveTable.SILVER_ORDER_ITEMS);

            logger.info("OrderItemSilverJob completed successfully");
        } catch (Exception e) {
            logger.error("OrderItemSilverJob failed fatally!", e);
            throw new RuntimeException(e);
        } finally {
            if (spark != null) {
                spark.stop();
                logger.info("Spark Session Stopped.");
            }
        }
    }
}
