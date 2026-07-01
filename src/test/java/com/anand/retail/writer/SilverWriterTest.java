package com.anand.retail.writer;

import com.anand.retail.config.ConfigLoader;
import com.anand.retail.constants.LakehouseTable;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SilverWriterTest {

    private static SparkSession spark;

    // Resolved from src/test/resources/application.properties (silver.path),
    // same source of truth SilverWriter itself reads from.
    private static final String SILVER_WRITER_PATH = Paths.get(
            ConfigLoader.get("silver.path"),
            LakehouseTable.CUSTOMERS.getDirectoryName()
    ).toString();

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("SilverWriterTest")
                .master("local[1]")
                .config("spark.ui.enabled", false)
                .getOrCreate();
    }

    @AfterAll
    static void tearDown() {
        if (spark != null) {
            spark.stop();
        }
    }

    @Test
    void testWriteTableForCustomers() {
        SilverWriter silverWriter = new SilverWriter();

        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("customer_id", DataTypes.StringType, false),
                DataTypes.createStructField("customer_city", DataTypes.StringType, true),
                DataTypes.createStructField("customer_state", DataTypes.StringType, true)
        });

        List<Row> rows = Arrays.asList(
                RowFactory.create("c001", "Sao Paulo", "SP"),
                RowFactory.create("c002", "Rio De Janeiro", "RJ")
        );

        Dataset<Row> customerDf = spark.createDataFrame(rows, schema);

        silverWriter.writeTable(customerDf, LakehouseTable.CUSTOMERS);

        assertTrue(Files.exists(Paths.get(SILVER_WRITER_PATH)),
                "Silver Customers directory should exist.");

        Dataset<Row> readDf = spark.read().parquet(SILVER_WRITER_PATH);
        assertEquals(2, readDf.count(),
                "Silver Customers DataFrame should have 2 rows.");
    }
}
