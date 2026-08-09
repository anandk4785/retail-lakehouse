package com.anand.retail.main;

import com.anand.retail.constants.LakehouseTable;
import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.reader.OrderItemReader;
import com.anand.retail.service.OrderItemService;
import com.anand.retail.writer.BronzeWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderItemBronzeJob {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemBronzeJob.class);

    public static void main(String[] args) {
        logger.info("Starting Order Item Bronze Ingestion Job");

        SparkSession spark = SparkSessionFactory.getSparkSession();

        try {
            OrderItemReader orderItemReader = new OrderItemReader();
            OrderItemService orderItemService = new OrderItemService(orderItemReader);

            Dataset<Row> orderItemDf = orderItemService.loadAndShowOrderItems(spark);

            BronzeWriter bronzeWriter = new BronzeWriter();
            bronzeWriter.writeTable(orderItemDf, LakehouseTable.ORDER_ITEMS);

            logger.info("Order Item Bronze Job Completed Sucessfully");
        }   catch (Exception e) {
            logger.error("Order Item Bronze Job failed fatally!", e);
            throw new RuntimeException(e);
        }   finally {
            if (spark != null) {
                spark.stop();
                logger.info("Spark Session Stopped.");
            }
        }
    }
}
