package com.anand.retail.service;

import com.anand.retail.reader.ProductReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceTest {

    private static SparkSession spark;

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("ProductServiceTest")
                .master("local[1]")
                .config("spark.ui.enabled", "false")
                .getOrCreate();
    }

    @AfterAll
    static void tearDown() {
        if (spark != null) {
            spark.stop();
        }
    }

    @Test
    void testLoadAndShowProducts() {
        try {
            ProductReader productReader = new ProductReader();
            ProductService productService = new ProductService(productReader);

            Dataset<Row> productDf = productService.loadAndShowProducts(spark);

            assertNotNull(productDf, "Result Dataframe should not be null");
            assertTrue(productDf.columns().length > 0, "Result Dataframe should have columns");
        } catch (Exception e) {
            fail("Service method should not throw exception : " + e.getMessage());
        }
    }
}
