package com.anand.retail.validator;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductValidatorTest {

    private static SparkSession spark;
    private static ProductValidator validator;

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("ProductValidatorTest")
                .master("local[1]")
                .config("spark.ui.enabled", "false")
                .getOrCreate();
        validator = new ProductValidator();
    }

    @AfterAll
    static void tearDown() {
        if (spark != null) {
            spark.stop();
        }
    }

    @Test
    void shouldRemoveNullPrimaryKeysAndDuplicates() {
        StructType schema = new StructType()
                .add("product_id", DataTypes.StringType, true)
                .add("product_category_name", DataTypes.StringType, true);

        List<Row> data = Arrays.asList(
                RowFactory.create("P-001", "Electronics"),
                RowFactory.create("P-002", "Furniture"),
                RowFactory.create("P-001", "Electronics"), // Duplicate
                RowFactory.create(null, "Toys")            // null PKs
        );

        Dataset<Row> testDf = spark.createDataFrame(data, schema);

        Dataset<Row> resultDf = validator.validate(testDf);

        assertEquals(2, resultDf.count(), "Expected 2 valid rows after validation");
    }
}
