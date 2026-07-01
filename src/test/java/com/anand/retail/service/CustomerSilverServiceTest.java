package com.anand.retail.service;

import com.anand.retail.config.ConfigLoader;
import com.anand.retail.constants.LakehouseTable;
import com.anand.retail.reader.BronzeReader;
import com.anand.retail.transform.CustomerTransformer;
import com.anand.retail.validator.CustomerValidator;
import com.anand.retail.writer.BronzeWriter;
import com.anand.retail.writer.SilverWriter;
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

public class CustomerSilverServiceTest {

    private static SparkSession spark;

    private static final String SILVER_CUSTOMERS_PATH = Paths.get(
            ConfigLoader.get("silver.path"),
            LakehouseTable.CUSTOMERS.getDirectoryName()
    ).toString();

    @BeforeAll
    static void setup() {
        spark = SparkSession
                .builder()
                .appName("CustomerSilverServiceTest")
                .master("local[1]")
                .config("spark.ui.enabled", "false")
                .getOrCreate();

        // Seed the Bronze layer ourselves so this test is self-contained and
        // does not depend on CustomerBronzeJob having been run beforehand.
        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("customer_id", DataTypes.StringType, true),
                DataTypes.createStructField("customer_city", DataTypes.StringType, true),
                DataTypes.createStructField("customer_state", DataTypes.StringType, true)
        });

        List<Row> rows = Arrays.asList(
                RowFactory.create("c001", "  sao paulo  ", "sp"),
                RowFactory.create("c001", "  sao paulo  ", "sp"), // duplicate id
                RowFactory.create("c002", "rio de janeiro", "rj"),
                RowFactory.create(null, "belo horizonte", "mg")   // null PK
        );

        Dataset<Row> bronzeSeedDf = spark.createDataFrame(rows, schema);

        new BronzeWriter().writeTable(bronzeSeedDf, LakehouseTable.CUSTOMERS);
    }

    @AfterAll
    static void tearDown() {
        if (spark != null) {
            spark.stop();
        }
    }

    @Test
    void shouldRunFullBronzeToSilverPipeline() {
        CustomerSilverService service = new CustomerSilverService(
                new BronzeReader(),
                new CustomerTransformer(),
                new CustomerValidator(),
                new SilverWriter()
        );

        service.run(spark);

        assertTrue(Files.exists(Paths.get(SILVER_CUSTOMERS_PATH)),
                "Silver customers directory should exist after running the service");

        Dataset<Row> silverDf = spark.read().parquet(SILVER_CUSTOMERS_PATH);

        // Null PK dropped, duplicate id collapsed -> 2 distinct customers remain
        assertEquals(2, silverDf.count());

        Row sp = silverDf.filter("customer_id = 'c001'").first();
        assertEquals("SP", sp.getAs("customer_state"));
        assertEquals("Sao Paulo", sp.getAs("customer_city"));
    }
}
