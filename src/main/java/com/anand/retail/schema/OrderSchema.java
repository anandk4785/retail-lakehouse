package com.anand.retail.schema;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public final class OrderSchema {

    public static final String ORDER_ID = "order_id";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String ORDER_STATUS = "order_status";
    public static final String ORDER_PURCHASE_TIMESTAMP = "order_purchase_timestamp";
    public static final String ORDER_APPROVED_AT = "order_approved_at";
    public static final String ORDER_DELIVERED_CARRIER_DATE = "order_delivered_carrier_date";
    public static final String ORDER_DELIVERED_CUSTOMER_DATE = "order_delivered_customer_date";
    public static final String ORDER_ESTIMATED_DELIVERY_DATE = "order_estimated_delivery_date";

    private OrderSchema() {

    }

    public static StructType getSchema() {
        return new StructType()
                .add(ORDER_ID, DataTypes.StringType, false)
                .add(CUSTOMER_ID, DataTypes.StringType, false)
                .add(ORDER_STATUS, DataTypes.StringType, true)
                .add(ORDER_PURCHASE_TIMESTAMP, DataTypes.TimestampType, true)
                .add(ORDER_APPROVED_AT, DataTypes.TimestampType, true)
                .add(ORDER_DELIVERED_CARRIER_DATE, DataTypes.TimestampType, true)
                .add(ORDER_DELIVERED_CUSTOMER_DATE, DataTypes.TimestampType, true)
                .add(ORDER_ESTIMATED_DELIVERY_DATE, DataTypes.TimestampType, true);
    }
}
