package com.anand.retail.schema;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public final class CustomerSchema {

    public static final String CUSTOMER_ID = "customer_id";
    public static final String CUSTOMER_UNIQUE_ID = "customer_unique_id";
    public static final String CUSTOMER_ZIP_CODE_PREFIX = "customer_zip_code_prefix";
    public static final String CUSTOMER_CITY = "customer_city";
    public static final String CUSTOMER_STATE = "customer_state";

    private CustomerSchema() {

    }

    public static StructType getSchema() {
        return new StructType()
                .add(CUSTOMER_ID, DataTypes.StringType, false)
                .add(CUSTOMER_UNIQUE_ID, DataTypes.StringType, false)
                .add(CUSTOMER_ZIP_CODE_PREFIX, DataTypes.StringType, true)
                .add(CUSTOMER_CITY, DataTypes.StringType, true)
                .add(CUSTOMER_STATE, DataTypes.StringType, true);
    }
}