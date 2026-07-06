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

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderValidatorTest {

    private static SparkSession spark;
    private static OrderValidator validator;

    private static final Timestamp PURCHASE_TS = Timestamp.valueOf("2018-01-01 10:00:00");

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("OrderValidatorTest")
                .master("local[1]")
                .config("spark.ui.enabled", "false")
                .getOrCreate();
        validator = new OrderValidator();
    }

    @AfterAll
    static void tearDown() {
        if (spark != null) {
            spark.stop();
        }
    }

    private StructType schema() {
        return new StructType()
                .add("order_id", DataTypes.StringType, true)
                .add("customer_id", DataTypes.StringType, true)
                .add("order_purchase_timestamp", DataTypes.TimestampType, true);
    }

    @Test
    void shouldRemoveNullOrderIdCustomerIdOrPurchaseTimestampAndDuplicates() {
        List<Row> data = Arrays.asList(
                RowFactory.create("O-001", "C-001", PURCHASE_TS),
                RowFactory.create("O-002", "C-002", PURCHASE_TS),
                RowFactory.create("O-001", "C-001", PURCHASE_TS), // duplicate order_id
                RowFactory.create(null, "C-003", PURCHASE_TS),    // null order_id
                RowFactory.create("O-004", null, PURCHASE_TS),    // null customer_id
                RowFactory.create("O-005", "C-005", null)         // null purchase_timestamp
        );

        Dataset<Row> testDf = spark.createDataFrame(data, schema());
        Dataset<Row> resultDf = validator.validate(testDf);

        assertEquals(2, resultDf.count(),
                "Expected only O-001 and O-002 to survive validation");
    }

    @Test
    void shouldKeepRowsWithAllRequiredFieldsPresent() {
        List<Row> data = Arrays.asList(
                RowFactory.create("O-001", "C-001", PURCHASE_TS),
                RowFactory.create("O-002", "C-002", PURCHASE_TS)
        );

        Dataset<Row> testDf = spark.createDataFrame(data, schema());
        Dataset<Row> resultDf = validator.validate(testDf);

        assertEquals(2, resultDf.count(), "Fully valid rows must not be dropped");
    }
}