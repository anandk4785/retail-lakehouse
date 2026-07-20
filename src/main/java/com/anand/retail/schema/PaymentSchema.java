package com.anand.retail.schema;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public final class PaymentSchema {

    public static final String ORDER_ID = "order_id";
    public static final String PAYMENT_SEQUENTIAL = "payment_sequential";
    public static final String PAYMENT_TYPE = "payment_type";
    public static final String PAYMENT_INSTALLMENTS = "payment_installments";
    public static final String PAYMENT_VALUE = "payment_value";

    private PaymentSchema() {

    }

    public static StructType getSchema() {
        return new StructType()
                .add(ORDER_ID, DataTypes.StringType, false)
                .add(PAYMENT_SEQUENTIAL, DataTypes.IntegerType, true)
                .add(PAYMENT_TYPE, DataTypes.StringType, true)
                .add(PAYMENT_INSTALLMENTS, DataTypes.IntegerType, true)
                .add(PAYMENT_VALUE, DataTypes.DoubleType, true);
    }
}
