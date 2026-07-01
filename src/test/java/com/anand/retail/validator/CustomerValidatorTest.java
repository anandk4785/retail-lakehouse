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

public class CustomerValidatorTest {

    private static SparkSession spark;
    private static CustomerValidator validator;

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("CustomerValidatorTest")
                .master("local[1]")
                .config("spark.ui.enabled", "false")
                .getOrCreate();

        validator = new CustomerValidator();
    }

    @AfterAll
    static void tearDown() {
        if (spark != null) {
            spark.stop();
        }
    }

    @Test
    void shouldRemoveNullPrimaryKeysAndDuplicates() {
        // 1. Define a minimal schema for testing
        StructType schema = new StructType()
                .add("customer_id", DataTypes.StringType, true)
                .add("customer_city", DataTypes.StringType, true);

        // 2. Create dirty test data: 1 valid, 1 duplicate ID, 1 null ID
        List<Row> data = Arrays.asList(
                RowFactory.create("ID-123", "Sau Paulo"),
                RowFactory.create("ID-123", "Rio de Janeiro"),
                RowFactory.create(null, "Campinas")
        );

        Dataset<Row> testDf = spark.createDataFrame(data, schema);

        // 3. Apply the validation logic
        Dataset<Row> resultDf = validator.validate(testDf);

        // 4. Assert the results (Only 1 record should survive)
        /*
         * In Spark, the .count() method on a Dataset always returns a long.
         * If we use
         * assertEquals(1, resultDf.count(), "...");
         * Java interprets the 1 as an int. JUnit 5 compares the expected Integer (1) against the actual Long (1L).
         * Because they are technically different object types in memory, JUnit strictly fails the assertion,
         * even though the numeric values are identical. That's why,
         * we use 1L to explicitly indicate that the expected value is a long, matching the return type of .count().
         */
        assertEquals(1L, resultDf.count(), "Validator should drop null IDs and keep only one unique ID");
    }
}
