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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentTransformerTest {

    private static SparkSession spark;
    private static PaymentTransformer transformer;

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("PaymentTransformerTest")
                .master("local[1]")
                .config("spark.ui.enabled", "false")
                .getOrCreate();

        transformer = new PaymentTransformer();
    }

    @AfterAll
    static void tearDown() {
        if (spark != null) {
            spark.stop();
        }
    }

    private StructType schema() {
        return new StructType()
                .add("order_id", DataTypes.StringType, false)
                .add("payment_sequential", DataTypes.IntegerType, true)
                .add("payment_type", DataTypes.StringType, true)
                .add("payment_installments", DataTypes.IntegerType, true)
                .add("payment_value", DataTypes.DoubleType, true);
    }

    @Test
    void shouldFlagInstallmentPaymentsWhenInstallmentsGreaterThanOne() {
        Row orders = RowFactory.create("O-001", 1, "credit_card", 3, 100.0);

        Dataset<Row> testDf = spark.createDataFrame(List.of(orders), schema());

        Row result = transformer.transform(testDf).first();

        assertTrue((boolean) result.getAs("is_installment_payment"),
                "3 installments should be flagged as an installment payment");
    }

    @Test
    void shouldNotFlagSingleInstallmentPaymentsAsInstallment() {
        Row orders = RowFactory.create("O-002", 1, "credit_card", 1, 50.0);

        Dataset<Row> testDf = spark.createDataFrame(List.of(orders), schema());

        Row result = transformer.transform(testDf).first();

        assertFalse((boolean) result.getAs("is_installment_payment"),
                "1 installment should not be flagged as an installment payment");
    }

    @Test
    void shouldFlagNullInstallmentsAsFalse() {
        Row order = RowFactory.create("O-008", 1, "credit_card", null, 100.0);

        Dataset<Row> testDf = spark.createDataFrame(List.of(order), schema());

        Row result = transformer.transform(testDf).first();

        assertFalse((boolean) result.getAs("is_installment_payment"),
                "Null installments should be flagged as not an installment payment");
    }

    @Test
    void shouldFlagCreditCardPaymentType() {
        Row orders = RowFactory.create("O-003", 1, "credit_card", 2, 75.0);

        Dataset<Row> testDf = spark.createDataFrame(List.of(orders), schema());

        Row result = transformer.transform(testDf).first();

        assertTrue((boolean) result.getAs("is_credit_card"));
    }

    @Test
    void shouldNotFlagNonCreditCardPaymentTypes() {
        Row boleto = RowFactory.create("O-004", 1, "boleto", 1, 30.0);
        Row voucher =RowFactory.create("O-005", 1, "voucher", 1, 20.0);

        Dataset<Row> testDf = spark.createDataFrame(Arrays.asList(boleto, voucher), schema());

        Dataset<Row> resultDf = transformer.transform(testDf);

        boolean anyFlaggedAsCreditCard = resultDf
                .filter(resultDf.col("is_credit_card").equalTo(true))
                .count() > 0;

        assertFalse((boolean) anyFlaggedAsCreditCard,
                "Neither voucher nor boleto payments should be flagged as credit card");
    }

    @Test
    void shouldRenamePaymentValueToPaymentAmountPreservingValue() {
        Row order = RowFactory.create("O-006", 1, "credit_card", 2, 150.0);

        Dataset<Row> testDf = spark.createDataFrame(List.of(order), schema());

        Row result = transformer.transform(testDf).first();

        assertEquals(150.0, result.getAs("payment_amount"),
                "Payment value should be preserved in the renamed column");
    }

    @Test
    void shouldProduceExpectedFinalColumnSetAndDropOriginalPaymentValueName() {
        Row order = RowFactory.create("O-007", 1, "credit_card", 2, 200.0);

        Dataset<Row> testDf = spark.createDataFrame(List.of(order), schema());

        List<String> cols = Arrays.asList(transformer.transform(testDf).columns());

        assertTrue(cols.contains("order_id"));
        assertTrue(cols.contains("payment_sequential"));
        assertTrue(cols.contains("payment_type"));
        assertTrue(cols.contains("payment_installments"));
        assertTrue(cols.contains("payment_amount"));
        assertTrue(cols.contains("is_installment_payment"));
        assertTrue(cols.contains("is_credit_card"));

        assertFalse(cols.contains("payment_value"),
                "Original payment_value column should be dropped in the final output");
    }

    @Test
    void shouldNotAlterRowCount() {
        Row order1 = RowFactory.create("O-001", 1, "credit_card", 3, 50.0);
        Row order2 = RowFactory.create("O-001", 1, "credit_card", 3, 50.0);

        Dataset<Row> testDf = spark.createDataFrame(Arrays.asList(order1, order2), schema());

        long count = transformer.transform(testDf).count();

        assertEquals(2, count,
                "PaymentTransformer must not drop or dedup rows; " +
                        "it only adds flags and renames columns");
    }


}
