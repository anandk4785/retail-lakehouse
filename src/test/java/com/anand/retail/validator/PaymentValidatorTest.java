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

public class PaymentValidatorTest {

    private static SparkSession spark;
    private static PaymentValidator validator;

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("PaymentValidatorTest")
                .master("local[1]")
                .config("spark.ui.enabled", "false")
                .getOrCreate();

        validator = new PaymentValidator();
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
                .add("payment_sequential", DataTypes.IntegerType, true)
                .add("payment_type", DataTypes.StringType, true)
                .add("payment_installments", DataTypes.IntegerType, true)
                .add("payment_value", DataTypes.DoubleType, true);
    }

    @Test
    void shouldRemoveNullOrderId() {
        List<Row> data = Arrays.asList(
                RowFactory.create("O-001", 1, "credit_card", 3, 100.0),
                RowFactory.create(null, 1, "credit_card", 3, 100.0)
        );

        Dataset<Row> testDf = spark.createDataFrame(data, schema());

        assertEquals(1, validator.validate(testDf).count());
    }

    @Test
    void shouldRemoveNullPaymentSequential() {
        List<Row> data = Arrays.asList(
                RowFactory.create("O-001", 1, "credit_card", 3, 100.0),
                RowFactory.create("O-002", null, "credit_card", 3, 100.0)
        );

        Dataset<Row> testDf = spark.createDataFrame(data, schema());

        assertEquals(1, validator.validate(testDf).count());
    }

    @Test
    void shouldRemoveNullPaymentType() {
        List<Row> data = Arrays.asList(
                RowFactory.create("O-001", 1, "credit_card", 3, 100.0),
                RowFactory.create("O-002", 1, null, 3, 100.0)
        );

        Dataset<Row> testDf = spark.createDataFrame(data, schema());

        assertEquals(1, validator.validate(testDf).count());
    }

    @Test
    void shouldRemoveNegativePaymentValue() {
        List<Row> data = Arrays.asList(
                RowFactory.create("O-001", 1, "credit_card", 3, 100.0),
                RowFactory.create("O-002", 1, "credit_card", 3, -50.0)
        );

        Dataset<Row> testDf = spark.createDataFrame(data, schema());

        assertEquals(1, validator.validate(testDf).count());
    }

    @Test
    void shouldKeepZeroValuePaymentsForVoucherAndDropNotDefinedTypes() {
        List<Row> data = Arrays.asList(
                RowFactory.create("O-001", 1, "credit_card", 3, 100.0),
                RowFactory.create("O-002", 1, "voucher", 1, 0.0),
                RowFactory.create("O-003", 1, "not_defined", 1, 0.0)
        );

        Dataset<Row> testDf = spark.createDataFrame(data, schema());

        assertEquals(2, validator.validate(testDf).count(),
                "Zero-value voucher should be kept, but not_defined should be dropped");
    }

    @Test
    void shouldKeepMultipleInstallmentRowsForTheSameOrder() {
        List<Row> data = Arrays.asList(
                RowFactory.create("O-001", 1, "credit_card", 3, 100.0),
                RowFactory.create("O-001", 2, "credit_card", 3, 100.0),
                RowFactory.create("O-001", 3, "credit_card", 3, 100.0)
        );

        Dataset<Row> testDf = spark.createDataFrame(data, schema());

        assertEquals(3, validator.validate(testDf).count(),
                "All three installments for O-001 must survive — " +
                        "they share order_id but not payment_sequential");
    }

    @Test
    void shouldDropTrueDuplicatesOnTheCompositeKey() {
        List<Row> data = Arrays.asList(
                RowFactory.create("O-001", 1, "credit_card", 3, 50.0),
                RowFactory.create("O-001", 1, "credit_card", 3, 50.0)
        );

        Dataset<Row> testDf = spark.createDataFrame(data, schema());

        assertEquals(1, validator.validate(testDf).count(),
                "Rows sharing both order_id and payment_sequential " +
                        "are true duplicates and must collapse to one");
    }

    @Test
    void shouldKeepAllValidUniqueRecords() {
        List<Row> data = Arrays.asList(
                RowFactory.create("O-001", 1, "credit_card", 3, 100.0),
                RowFactory.create("O-002", 1, "boleto", 1, 50.0),
                RowFactory.create("O-002", 2, "voucher", 1, 25.0)
        );

        Dataset<Row> testDf = spark.createDataFrame(data, schema());

        assertEquals(3, validator.validate(testDf).count());
    }
}
