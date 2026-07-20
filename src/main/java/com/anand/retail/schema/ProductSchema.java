package com.anand.retail.schema;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public final class ProductSchema {

    public static final String PRODUCT_ID = "product_id";
    public static final String PRODUCT_CATEGORY_NAME = "product_category_name";
    public static final String PRODUCT_NAME_LENGTH = "product_name_lenght";
    public static final String PRODUCT_DESCRIPTION_LENGTH = "product_description_lenght";
    public static final String PRODUCT_PHOTOS_QTY = "product_photos_qty";
    public static final String PRODUCT_WEIGHT_G = "product_weight_g";
    public static final String PRODUCT_LENGTH_CM = "product_length_cm";
    public static final String PRODUCT_HEIGHT_CM = "product_height_cm";
    public static final String PRODUCT_WIDTH_CM = "product_width_cm";

    private ProductSchema() {

    }

    public static StructType getSchema() {
        return new StructType()
                .add(PRODUCT_ID, DataTypes.StringType, false)
                .add(PRODUCT_CATEGORY_NAME, DataTypes.StringType, true)
                .add(PRODUCT_NAME_LENGTH, DataTypes.IntegerType, true)
                .add(PRODUCT_DESCRIPTION_LENGTH, DataTypes.IntegerType, true)
                .add(PRODUCT_PHOTOS_QTY, DataTypes.IntegerType, true)
                .add(PRODUCT_WEIGHT_G, DataTypes.DoubleType, true)
                .add(PRODUCT_LENGTH_CM, DataTypes.DoubleType, true)
                .add(PRODUCT_HEIGHT_CM, DataTypes.DoubleType, true)
                .add(PRODUCT_WIDTH_CM, DataTypes.DoubleType, true);
    }
}
