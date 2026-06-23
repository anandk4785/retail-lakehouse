package com.anand.retail.schema;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public final class OrderSchema {

    private OrderSchema() {

    }

    public static StructType getSchema() {
        return new StructType()
                .add("order_id", DataTypes.StringType, false)
                .add("customer_id", DataTypes.StringType, false)
                .add("order_status", DataTypes.StringType, true)
                .add("order_purchase_timestamp", DataTypes.TimestampType, true)
                .add("order_approved_at", DataTypes.TimestampType, true)
                .add("order_delivered_carrier_date", DataTypes.TimestampType, true)
                .add("order_delivered_customer_date", DataTypes.TimestampType, true)
                .add("order_estimated_delivery_date", DataTypes.TimestampType, true);
    }
}
