package com.anand.retail.schema;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public final class ProductSchema {

    private ProductSchema() {

    }

    public static StructType getSchema() {
        return new StructType()
                .add("product_id", DataTypes.StringType, false)
                .add("product_category_name", DataTypes.StringType, true)
                .add("product_name_lenght", DataTypes.IntegerType, true)
                .add("product_description_lenght", DataTypes.IntegerType, true)
                .add("product_photos_qty", DataTypes.IntegerType, true)
                .add("product_weight_g", DataTypes.DoubleType, true)
                .add("product_length_cm", DataTypes.DoubleType, true)
                .add("product_height_cm", DataTypes.DoubleType, true)
                .add("product_width_cm", DataTypes.DoubleType, true);
    }
}
