package com.anand.retail.schema;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public final class OrderItemSchema {

    public static final String ORDER_ID = "order_id";
    public static final String ORDER_ITEM_ID = "order_item_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String SELLER_ID = "seller_id";
    public static final String SHIPPING_LIMIT_DATE = "shipping_limit_date";
    public static final String PRICE = "price";
    public static final String FREIGHT_VALUE = "freight_value";

    private OrderItemSchema() {

    }

    public static StructType getSchema() {
        return new StructType()
                .add(ORDER_ID, DataTypes.StringType, false)
                .add(ORDER_ITEM_ID, DataTypes.LongType, false)
                .add(PRODUCT_ID, DataTypes.StringType, true)
                .add(SELLER_ID, DataTypes.StringType, true)
                .add(SHIPPING_LIMIT_DATE, DataTypes.TimestampType, true)
                .add(PRICE, DataTypes.DoubleType, true)
                .add(FREIGHT_VALUE, DataTypes.DoubleType, true);
    }
}
