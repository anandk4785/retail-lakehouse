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

public class OrderItemTransformerTest {

    private static SparkSession spark;
    private static final OrderItemTransformer transformer = new OrderItemTransformer();

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("OrderItemTransformerTest")
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
    void shouldReturnDataFrameUnchanged() {
        StructType schema = new StructType()
                .add("order_id", DataTypes.StringType, true)
                .add("price", DataTypes.DoubleType, true);

        List<Row> data = Arrays.asList(
                RowFactory.create("O-001", 10.50)
        );

        Dataset<Row> inputDf = spark.createDataFrame(data, schema);
        Dataset<Row> resultDf = transformer.transform(inputDf);

        assertEquals(1L, resultDf.count());
        assertEquals("O-001", resultDf.first().getAs("order_id"));
    }
}
