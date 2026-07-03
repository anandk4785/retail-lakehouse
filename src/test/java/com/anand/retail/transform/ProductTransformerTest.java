package com.anand.retail.transform;

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

public class ProductTransformerTest {

    private static SparkSession spark;
    private static ProductTransformer transformer;

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("ProductTransformerTest")
                .master("local[1]")
                .config("spark.ui.enabled", "false")
                .getOrCreate();
        transformer = new ProductTransformer();
    }

    @AfterAll
    static void teardown() {
        if (spark != null) {
            spark.stop();
        }
    }

    @Test
    void shouldFillNullCategoriesWithUnknown() {
        StructType schema = new StructType()
                .add("product_id", DataTypes.StringType, false)
                .add("product_category_name", DataTypes.StringType, true);

        List<Row> data = Arrays.asList(
                RowFactory.create("P-001", "Sports"),
                RowFactory.create("P-002", null)
        );

        Dataset<Row> testDf = spark.createDataFrame(data, schema);

        Dataset<Row> resultDf = transformer.transform(testDf);

        String p1Category = resultDf.filter(resultDf.col("product_id").equalTo("P-001"))
                .select("product_category_name")
                .first()
                .getString(0);

        String p2Category = resultDf.filter("product_id = 'P-002'").first().getAs("product_category_name");

        assertEquals("Sports", p1Category, "Existing categories should not be modified");
        assertEquals("Unknown", p2Category, "Null categories should be replaced with 'Unknown'");
    }
}
