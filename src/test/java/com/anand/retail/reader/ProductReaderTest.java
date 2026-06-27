package com.anand.retail.reader;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductReaderTest {
    private static SparkSession spark;

    private static final ProductReader productReader = new ProductReader();

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("ProductReaderTest")
                .master("local[1]")
                .config("spark.ui.enabled", "false")
                .getOrCreate();
    }

    @AfterAll
    static void terrDown() {
        if (spark != null) {
            spark.stop();
        }
    }

    @Test
    void shouldReadProductDataset() {
        Dataset<Row> productDf = productReader.readProducts(spark);

        assertNotNull(productDf);
        assertTrue(productDf.count() > 0);
    }

    @Test
    void shouldContainExpectedColumns() {
        Dataset<Row> productDf = productReader.readProducts(spark);

        List<String> columns = Arrays.asList(productDf.columns());

        assertTrue(columns.contains("product_id"));
        assertTrue(columns.contains("product_category_name"));
        assertTrue(columns.contains("product_name_lenght"));
    }
}
