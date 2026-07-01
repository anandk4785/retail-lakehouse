package com.anand.retail.reader;

import com.anand.retail.config.ConfigLoader;
import com.anand.retail.constants.LakehouseTable;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BronzeReaderTest {

    private static SparkSession spark;

    private static final String BRONZE_CUSTOMERS_PATH = Paths.get(
            ConfigLoader.get("bronze.path"),
            LakehouseTable.CUSTOMERS.getDirectoryName()
    ).toString();

    private static final BronzeReader bronzeReader = new BronzeReader();

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("BronzeReaderTest")
                .master("local[1]")
                .config("spark.ui.enabled", "false")
                .getOrCreate();

        // Seed the Bronze layer directly so this test does not depend on the
        // external Olist CSVs or on CustomerBronzeJob having run first.
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("customer_id", DataTypes.StringType, false),
                DataTypes.createStructField("customer_city", DataTypes.StringType, true),
                DataTypes.createStructField("customer_state", DataTypes.StringType, true)
        });

        List<Row> rows = Arrays.asList(
                RowFactory.create("c001", "sao paulo", "sp"),
                RowFactory.create("c002", "rio de janeiro", "rj")
        );

        Dataset<Row> seedDf = spark.createDataFrame(rows, schema);

        seedDf.write()
                .mode(SaveMode.Overwrite)
                .parquet(BRONZE_CUSTOMERS_PATH);
    }

    @AfterAll
    static void tearDown() {
        if (spark != null) {
            spark.stop();
        }
    }

    @Test
    void shouldReadBronzeTableByEnum() {
        Dataset<Row> customerDf = bronzeReader.readTable(spark, LakehouseTable.CUSTOMERS);

        assertNotNull(customerDf);
        assertEquals(2, customerDf.count());
    }

    @Test
    void shouldResolvePathUsingDirectoryNameNotEnumName() {
        // Guards against a regression where String.valueOf(enum) ("CUSTOMERS")
        // is used instead of table.getDirectoryName() ("customers").
        Dataset<Row> customerDf = bronzeReader.readTable(spark, LakehouseTable.CUSTOMERS);

        List<String> columns = Arrays.asList(customerDf.columns());
        assertTrue(columns.contains("customer_id"));
    }
}
