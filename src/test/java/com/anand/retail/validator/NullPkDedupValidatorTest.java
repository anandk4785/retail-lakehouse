package com.anand.retail.validator;

import com.anand.retail.schema.CustomerSchema;
import com.anand.retail.schema.OrderItemSchema;
import com.anand.retail.schema.OrderSchema;
import com.anand.retail.schema.ProductSchema;
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

public class NullPkDedupValidatorTest {

    private static SparkSession spark;

    private static final Timestamp PURCHASE_TS =
            Timestamp.valueOf("2018-01-01 10:00:00");

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("NullPkDedupValidatorTest")
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
    void shouldValidateCustomerRowsUsingSingleRequiredAndDeduplicateColumn() {
        NullPkDedupValidator validator = new NullPkDedupValidator(
                new String[]{CustomerSchema.CUSTOMER_ID},
                new String[]{CustomerSchema.CUSTOMER_ID}
        );

        StructType schema = new StructType()
                .add(CustomerSchema.CUSTOMER_ID, DataTypes.StringType, true)
                .add(CustomerSchema.CUSTOMER_CITY, DataTypes.StringType, true);

        List<Row> data = Arrays.asList(
                RowFactory.create("C-001", "Sao Paulo"),
                RowFactory.create("C-001", "Rio de Janeiro"),
                RowFactory.create(null, "Campinas")
        );

        Dataset<Row> resultDf = validator.validate(
                spark.createDataFrame(data, schema)
        );

        assertEquals(1L, resultDf.count(),
                "Customer validation should drop null customer_id rows " +
                        "and collapse duplicate customer_id rows");
    }

    @Test
    void shouldValidateProductRowsUsingSingleRequiredAndDeduplicateColumn() {
        NullPkDedupValidator validator = new NullPkDedupValidator(
                new String[]{ProductSchema.PRODUCT_ID},
                new String[]{ProductSchema.PRODUCT_ID}
        );

        StructType schema = new StructType()
                .add(ProductSchema.PRODUCT_ID, DataTypes.StringType, true)
                .add(ProductSchema.PRODUCT_CATEGORY_NAME, DataTypes.StringType, true);

        List<Row> data = Arrays.asList(
                RowFactory.create("P-001", "Electronics"),
                RowFactory.create("P-002", "Furniture"),
                RowFactory.create("P-001", "Electronics"),
                RowFactory.create(null, "Toys")
        );

        Dataset<Row> resultDf = validator.validate(
                spark.createDataFrame(data, schema)
        );

        assertEquals(2L, resultDf.count(),
                "Product validation should drop null product_id rows " +
                        "and collapse duplicate product_id rows");
    }

    @Test
    void shouldValidateOrderRowsUsingMultipleRequiredColumnsAndSingleDeduplicateColumn() {
        NullPkDedupValidator validator = new NullPkDedupValidator(
                new String[]{
                        OrderSchema.ORDER_ID,
                        OrderSchema.CUSTOMER_ID,
                        OrderSchema.ORDER_PURCHASE_TIMESTAMP
                },
                new String[]{OrderSchema.ORDER_ID}
        );

        StructType schema = new StructType()
                .add(OrderSchema.ORDER_ID, DataTypes.StringType, true)
                .add(OrderSchema.CUSTOMER_ID, DataTypes.StringType, true)
                .add(OrderSchema.ORDER_PURCHASE_TIMESTAMP, DataTypes.TimestampType, true);

        List<Row> data = Arrays.asList(
                RowFactory.create("O-001", "C-001", PURCHASE_TS),
                RowFactory.create("O-002", "C-002", PURCHASE_TS),
                RowFactory.create("O-001", "C-001", PURCHASE_TS),
                RowFactory.create(null, "C-003", PURCHASE_TS),
                RowFactory.create("O-004", null, PURCHASE_TS),
                RowFactory.create("O-005", "C-005", null)
        );

        Dataset<Row> resultDf = validator.validate(
                spark.createDataFrame(data, schema)
        );

        assertEquals(2L, resultDf.count(),
                "Order validation should require order_id, customer_id, and purchase timestamp, then deduplicate by order_id");
    }

    @Test
    void shouldValidateOrderItemRowsUsingCompositeDeduplicateKey() {
        NullPkDedupValidator validator = new NullPkDedupValidator(
                new String[]{
                        OrderItemSchema.ORDER_ID,
                        OrderItemSchema.ORDER_ITEM_ID,
                        OrderItemSchema.PRODUCT_ID
                },
                new String[]{
                        OrderItemSchema.ORDER_ID,
                        OrderItemSchema.ORDER_ITEM_ID
                }
        );

        StructType schema = new StructType()
                .add(OrderItemSchema.ORDER_ID, DataTypes.StringType, true)
                .add(OrderItemSchema.ORDER_ITEM_ID, DataTypes.LongType, true)
                .add(OrderItemSchema.PRODUCT_ID, DataTypes.StringType, true);

        List<Row> data = Arrays.asList(
                // Two different items in the same order (should BOTH survive)
                RowFactory.create("O-001", 1L, "P-001"),
                RowFactory.create("O-001", 2L, "P-002"),
                // Duplicate of the first item (should be dropped)
                RowFactory.create("O-001", 1L, "P-001"),
                // Null required column (should be dropped)
                RowFactory.create("O-002", null, "P-003")
        );

        Dataset<Row> resultDf = validator.validate(
                spark.createDataFrame(data, schema)
        );

        assertEquals(2L, resultDf.count(),
                "Order item validation should survive multiple items per order but deduplicate exact order_id/order_item_id pairs");
    }
}