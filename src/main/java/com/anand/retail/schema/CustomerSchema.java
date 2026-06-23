package com.anand.retail.schema;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public final class CustomerSchema {
    private CustomerSchema() {

    }

    public static StructType getSchema() {
        return new StructType()
                .add("customer_id", DataTypes.StringType, false)
                .add("customer_unique_id", DataTypes.StringType, false)
                .add("customer_zip_code_prefix", DataTypes.StringType, true)
                .add("customer_city", DataTypes.StringType, true)
                .add("customer_state", DataTypes.StringType, true);
    }
}