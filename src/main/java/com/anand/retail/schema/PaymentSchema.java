package com.anand.retail.schema;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public final class PaymentSchema {

    private PaymentSchema() {

    }

    public static StructType getSchema() {
        return new StructType()
                .add("order_id", DataTypes.StringType, false)
                .add("payment_sequential", DataTypes.IntegerType, true)
                .add("payment_type", DataTypes.StringType, true)
                .add("payment_installments", DataTypes.IntegerType, true)
                .add("payment_value", DataTypes.DoubleType, true);
    }
}
