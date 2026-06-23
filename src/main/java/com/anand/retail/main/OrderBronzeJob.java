package com.anand.retail.main;

import com.anand.retail.constants.LakehouseTable;
import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.reader.OrderReader;
import com.anand.retail.service.OrderService;
import com.anand.retail.writer.BronzeWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderBronzeJob {

    private static final Logger logger =
            LoggerFactory.getLogger(OrderBronzeJob.class);

    public static void main(String[] args) {
        logger.info("Starting Order Bronze Ingestion Job");

        SparkSession spark = SparkSessionFactory.getSparkSession();

        try {
            // 1. Assembly Line (Dependency Injection)
            OrderReader orderReader = new OrderReader();
            OrderService orderService = new OrderService(orderReader);

            Dataset<Row> orderDf = orderService.loadAndShowOrders(spark);

            // 2. Universal Enum Write
            BronzeWriter bronzeWriter = new BronzeWriter();
            bronzeWriter.writeTable(orderDf, LakehouseTable.ORDERS);

            logger.info("Order Bronze Job Completed Successfully");

        } catch (Exception e) {
            logger.error("Order Bronze Job failed fatally!", e);

            throw new RuntimeException(e);
        } finally {
            if (spark != null) {
                spark.stop();
                logger.info("Spark Session stopped.");
            }
        }
    }
}
