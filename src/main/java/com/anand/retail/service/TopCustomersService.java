package com.anand.retail.service;

import com.anand.retail.constants.HiveTable;
import com.anand.retail.writer.HiveWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class TopCustomersService implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(TopCustomersService.class);

    private final HiveWriter writer;
    private final String targetDatabase;
    private final String targetTableName;

    public TopCustomersService(HiveWriter writer, String targetDatabase, String targetTableName) {
        this.writer = writer;
        this.targetDatabase = targetDatabase;
        this.targetTableName = targetTableName;
    }

    public void run(SparkSession spark) {
        logger.info("--- Starting Top Customers Gold Execution Flow ---");
        long startTime = System.currentTimeMillis();

        Dataset<Row> topCustomers = buildTopCustomers(spark);
        long outputCount = topCustomers.count();
        logger.info("Gold aggregate row count: {}", outputCount);

        writer.writeManagedTable(topCustomers, targetDatabase, targetTableName, SaveMode.Overwrite);

        long endTime = System.currentTimeMillis();
        logger.info("--- Top Customers Gold Execution Completed in {} ms ---", (endTime - startTime));
    }

    public Dataset<Row> buildTopCustomers(SparkSession spark) {
        logger.info("Building top customers report from  registered silver hive tables");

        String orderItems = HiveTable.SILVER_ORDER_ITEMS.getFullQualifiedName();
        String orders = HiveTable.SILVER_ORDERS.getFullQualifiedName();
        String customers = HiveTable.SILVER_CUSTOMERS.getFullQualifiedName();

        return spark.sql(
                "SELECT " +
                        "c.customer_id, " +
                        "c.customer_city, " +
                        "c.customer_state, " +
                        "COUNT(DISTINCT o.order_id) AS order_count, " +
                        "SUM(oi.price + oi.freight_value) AS total_spent, " +
                        "SUM(oi.price + oi.freight_value) / COUNT(DISTINCT o.order_id) AS avg_order_value, " +
                        "RANK() OVER (ORDER BY SUM(oi.price + oi.freight_value) DESC) AS customer_rank " +
                        "FROM " + orderItems + " oi " +
                        "INNER JOIN " + orders + " o ON oi.order_id = o.order_id " +
                        "INNER JOIN " + customers + " c ON o.customer_id = c.customer_id " +
                        "WHERE o.is_delivered = true " +
                        "GROUP BY c.customer_id, c.customer_city, c.customer_state " +
                        "ORDER BY total_spent DESC"
        );
    }
}
