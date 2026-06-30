package com.anand.retail.transform;

import com.anand.retail.factory.SparkSessionFactory;
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

public class CustomerTransformerTest {
    private static SparkSession spark;

    private static CustomerTransformer transformer;

    @BeforeAll
    static void setup() {
        spark = SparkSessionFactory.getSparkSession();
        transformer = new CustomerTransformer();
    }

    @AfterAll
    static void tearDown() {
        if (spark != null) {
            spark.stop();
        }
    }

    @Test
    void shouldCleanseCityAndState() {
        // 1. Define schema
        StructType schema = new StructType()
                .add("customer_id", DataTypes.StringType, true)
                .add("customer_city", DataTypes.StringType, true)
                .add("customer_state", DataTypes.StringType, true);

        // 2. Create test data with messy formatting (extra spaces, weird casing)
        List<Row> data = List.of(
                RowFactory.create("ID-123", "  sao PAULO  ", "  sp  ")
        );

        Dataset<Row> testDf = spark.createDataFrame(data, schema);

        // 3. Apply the transformation
        Dataset<Row> resultDf = transformer.transform(testDf);
        Row cleanedRow = resultDf.first();

        // 4. Assert the formatting was corrected
        assertEquals("Sao Paulo", cleanedRow.getAs("customer_city"), "City should be trimmed and InitCapped");
        assertEquals("SP", cleanedRow.getAs("customer_state"), "State should be trimmed and UPPERCASED");
    }
}
