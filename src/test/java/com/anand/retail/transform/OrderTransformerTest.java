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

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderTransformerTest {

    private static SparkSession spark;
    private static OrderTransformer transformer;

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("OrderTransformerTest")
                .master("local[1]")
                .config("spark.ui.enabled", "false")
                .getOrCreate();
        transformer = new OrderTransformer();
    }

    @AfterAll
    static void teardown() {
        if (spark != null) {
            spark.stop();
        }
    }

    private StructType schema() {
        return new StructType()
                .add("order_id", DataTypes.StringType, false)
                .add("customer_id", DataTypes.StringType, false)
                .add("order_status", DataTypes.StringType, true)
                .add("order_purchase_timestamp", DataTypes.TimestampType, true)
                .add("order_approved_at", DataTypes.TimestampType, true)
                .add("order_delivered_carrier_date", DataTypes.TimestampType, true)
                .add("order_delivered_customer_date", DataTypes.TimestampType, true)
                .add("order_estimated_delivery_date", DataTypes.TimestampType, true);
    }

    @Test
    void shouldComputeDateDimensionsAndDeliveryMetricsForADeliveredOrder() {
        Row deliveredOrder = RowFactory.create(
                "O-001",
                "C-001",
                "delivered",
                Timestamp.valueOf("2018-01-01 10:00:00"), // purchased
                Timestamp.valueOf("2018-01-02 08:00:00"), // approved (+1 day)
                Timestamp.valueOf("2018-01-03 09:00:00"), // dispatched to carrier (+1 day)
                Timestamp.valueOf("2018-01-10 14:00:00"), // delivered to customer (+7 days transit)
                Timestamp.valueOf("2018-01-15 00:00:00")  // estimated delivery (5 days later than actual)
        );

        Dataset<Row> testDf = spark.createDataFrame(List.of(deliveredOrder), schema());
        Row result = transformer.transform(testDf).first();

        assertEquals(java.sql.Date.valueOf("2018-01-01"), result.getAs("purchase_date"));
        assertEquals(2018, (int) result.getAs("purchase_year"));
        assertEquals(1, (int) result.getAs("purchase_month"));
        assertEquals(1, (int) result.getAs("purchase_day"));

        assertEquals(9, (int) result.getAs("delivery_days"),
                "Jan 1 -> Jan 10 should be 9 days");
        assertEquals(-5, (int) result.getAs("delivery_delay_days"),
                "Delivered 5 days before the estimated date should be negative");
        assertEquals(1, (int) result.getAs("approval_time_days"));
        assertEquals(1, (int) result.getAs("carrier_dispatch_days"));
        assertEquals(7, (int) result.getAs("carrier_transit_days"));

        assertEquals("delivered", result.getAs("status"));
        assertTrue((boolean) result.getAs("is_delivered"));
    }

    @Test
    void shouldLeaveDownstreamMetricsNullForAnUndeliveredOrder() {
        Row shippedOrder = RowFactory.create(
                "O-002",
                "C-002",
                "shipped",
                Timestamp.valueOf("2018-02-01 09:00:00"), // purchased
                Timestamp.valueOf("2018-02-01 12:00:00"), // approved same day
                Timestamp.valueOf("2018-02-02 08:00:00"), // dispatched to carrier (+1 day)
                null,                                       // not yet delivered to customer
                Timestamp.valueOf("2018-02-10 00:00:00")
        );

        Dataset<Row> testDf = spark.createDataFrame(List.of(shippedOrder), schema());
        Row result = transformer.transform(testDf).first();

        // Metrics depending on a null delivered-to-customer date must be null,
        // not silently coerced to 0 or dropped.
        assertNull(result.getAs("delivery_days"));
        assertNull(result.getAs("delivery_delay_days"));
        assertNull(result.getAs("carrier_transit_days"));

        // Metrics that don't depend on the missing date should still compute.
        assertEquals(0, (int) result.getAs("approval_time_days"));
        assertEquals(1, (int) result.getAs("carrier_dispatch_days"));

        assertEquals("shipped", result.getAs("status"));
        assertFalse((boolean) result.getAs("is_delivered"));
    }

    @Test
    void shouldRenameColumnsAndDropOriginalOrderPrefixedNames() {
        Row order = RowFactory.create(
                "O-003",
                "C-003",
                "delivered",
                Timestamp.valueOf("2018-01-01 10:00:00"),
                Timestamp.valueOf("2018-01-02 08:00:00"),
                Timestamp.valueOf("2018-01-03 09:00:00"),
                Timestamp.valueOf("2018-01-10 14:00:00"),
                Timestamp.valueOf("2018-01-15 00:00:00")
        );

        Dataset<Row> testDf = spark.createDataFrame(List.of(order), schema());
        List<String> columns = Arrays.asList(transformer.transform(testDf).columns());

        assertTrue(columns.contains("purchase_timestamp"));
        assertTrue(columns.contains("approved_timestamp"));
        assertTrue(columns.contains("carrier_dispatch_timestamp"));
        assertTrue(columns.contains("customer_delivered_timestamp"));
        assertTrue(columns.contains("estimated_delivery_timestamp"));
        assertTrue(columns.contains("status"));

        assertFalse(columns.contains("order_purchase_timestamp"),
                "Original column name should not survive the rename/select");
        assertFalse(columns.contains("order_status"),
                "Original column name should not survive the rename/select");
    }

    @Test
    void shouldNotAlterRowCount() {
        // Transformer only adds/renames columns; validation (null PK/FK/
        // purchase-timestamp checks) is OrderValidator's responsibility,
        // applied upstream.
        Row order1 = RowFactory.create(
                "O-001", "C-001", "delivered",
                Timestamp.valueOf("2018-01-01 10:00:00"),
                Timestamp.valueOf("2018-01-02 08:00:00"),
                Timestamp.valueOf("2018-01-03 09:00:00"),
                Timestamp.valueOf("2018-01-10 14:00:00"),
                Timestamp.valueOf("2018-01-15 00:00:00")
        );
        Row order2 = RowFactory.create(
                "O-001", "C-001", "delivered", // duplicate order_id
                Timestamp.valueOf("2018-01-01 10:00:00"),
                Timestamp.valueOf("2018-01-02 08:00:00"),
                Timestamp.valueOf("2018-01-03 09:00:00"),
                Timestamp.valueOf("2018-01-10 14:00:00"),
                Timestamp.valueOf("2018-01-15 00:00:00")
        );

        Dataset<Row> testDf = spark.createDataFrame(Arrays.asList(order1, order2), schema());
        long count = transformer.transform(testDf).count();

        assertEquals(2, count, "OrderTransformer must not drop or dedup rows");
    }
}