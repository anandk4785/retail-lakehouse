package com.anand.retail.service;

import com.anand.retail.reader.OrderItemReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class OrderItemService implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemService.class);

    private final OrderItemReader orderItemReader;

    public OrderItemService(OrderItemReader orderItemReader) {
        this.orderItemReader = orderItemReader;
    }

    public Dataset<Row> loadAndShowOrderItems(SparkSession spark) {
        Dataset<Row> orderItemDf = orderItemReader.readOrderItem(spark);

        logger.info("Printing schema for Order Items");
        orderItemDf.printSchema();

        logger.info("Showing sample order items records");
        orderItemDf.show(10, false);

        long count = orderItemDf.count();
        logger.info("Order item count: {}", count);

        return orderItemDf;
    }
}
