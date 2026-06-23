package com.anand.retail.service;

import com.anand.retail.reader.OrderReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

// 1. Implements Serializable to protect against Spark network serialization leaks
public class OrderService implements Serializable {

    private static final Logger logger =
            LoggerFactory.getLogger(OrderService.class);

    private final OrderReader orderReader;

    // 2. True Dependency Injection: We demand the Reader
    public OrderService(OrderReader orderReader) {
        this.orderReader = orderReader;
    }

    public Dataset<Row> loadAndShowOrders(SparkSession spark) {

        Dataset<Row> orderDf = orderReader.readOrders(spark);

        logger.info("Printing Schema for Orders");
        orderDf.printSchema();

        logger.info("Showing sample order records");
        orderDf.show(10, false);

        long count = orderDf.count();
        logger.info("Order Count : {}", count);

        return orderDf;
    }
}
