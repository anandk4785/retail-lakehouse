package com.anand.retail.transform;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.datediff;
import static org.apache.spark.sql.functions.day;
import static org.apache.spark.sql.functions.month;
import static org.apache.spark.sql.functions.to_date;
import static org.apache.spark.sql.functions.year;

public class OrderTransformer implements DataTransformer, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(OrderTransformer.class);

    @Override
    public Dataset<Row> transform(Dataset<Row> orderDf) {
        logger.info("Applying Silver transformations for Order Fact");

        return orderDf

                // Purchase Date Dimensions
                .withColumn("purchase_date",
                        to_date(col("order_purchase_timestamp")))
                .withColumn("purchase_year",
                        year(col("order_purchase_timestamp")))
                .withColumn("purchase_month",
                        month(col("order_purchase_timestamp")))
                .withColumn("purchase_day",
                        day(col("order_purchase_timestamp")))

                // Delivery Metrics
                .withColumn("delivery_days",
                        datediff(
                                col("order_delivered_customer_date"),
                                col("order_purchase_timestamp")))

                .withColumn("delivery_delay_days",
                        datediff(
                                col("order_delivered_customer_date"),
                                col("order_estimated_delivery_date")))

                // Processing Metrics
                .withColumn("approval_time_days",
                        datediff(
                                col("order_approved_at"),
                                col("order_purchase_timestamp")))

                .withColumn("carrier_dispatch_days",
                        datediff(
                                col("order_delivered_carrier_date"),
                                col("order_approved_at")))

                .withColumn("carrier_transit_days",
                        datediff(
                                col("order_delivered_customer_date"),
                                col("order_delivered_carrier_date")))
                .withColumn(
                        "is_delivered",
                        col("order_status")
                                .equalTo("delivered")
                )
                // Shape the final output and rename columns
                .select(
                        col("order_id"),
                        col("customer_id"),
                        col("order_purchase_timestamp").alias("purchase_timestamp"),
                        col("purchase_date"),
                        col("purchase_year"),
                        col("purchase_month"),
                        col("purchase_day"),
                        col("order_approved_at").alias("approved_timestamp"),
                        col("order_delivered_carrier_date").alias("carrier_dispatch_timestamp"),
                        col("order_delivered_customer_date").alias("customer_delivered_timestamp"),
                        col("order_estimated_delivery_date").alias("estimated_delivery_timestamp"),
                        col("delivery_days"),
                        col("delivery_delay_days"),
                        col("approval_time_days"),
                        col("carrier_dispatch_days"),
                        col("carrier_transit_days"),
                        col("order_status").alias("status"),
                        col("is_delivered")
                );

    }
}
