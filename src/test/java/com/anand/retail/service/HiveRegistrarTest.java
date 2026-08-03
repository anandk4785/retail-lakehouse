package com.anand.retail.service;

import com.anand.retail.config.ConfigLoader;
import com.anand.retail.constants.HiveTable;
import com.anand.retail.constants.LakehouseTable;
import com.anand.retail.factory.SparkSessionFactory;
import com.anand.retail.writer.SilverWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for the full Hive registration loop: a real
 * SparkSession with Hive support (via SparkSessionFactory), a real
 * "silver" database, a real Silver Parquet dataset (via SilverWriter),
 * and HiveRegistrar cataloging it — then verifying the result is
 * actually queryable through Spark SQL, not just that no exception was
 * thrown.
 *
 * Deliberately uses SparkSessionFactory.getSparkSession() rather than a
 * fresh SparkSession.builder() call, since that's the exact session
 * every *SilverJob runs against in production (warehouse dir, Derby
 * metastore path, datanucleus.schema.autoCreateAll). Testing through an
 * independently-configured session would risk validating behavior
 * nothing in production actually exercises.
 *
 * SparkSessionFactory manages the session as a JVM-wide singleton, but is
 * robust against teardowns and will recreate the session if stopped. We
 * include an @AfterAll spark.stop() here to align with the rest of the
 * test suite and ensure clean test boundaries.
 */
public class HiveRegistrarTest {

    private static SparkSession spark;

    @BeforeAll
    static void setup() {
        spark = SparkSessionFactory.getSparkSession();

        // Ensure the silver database exists. Mirrors what HiveSetupJob
        // does in production; kept minimal here (just "silver", not the
        // full bronze/silver/gold sweep) since that's all this test needs.
        String silverPath = ConfigLoader.get("silver.path");
        String locationUri = new File(silverPath).toURI().toString();
        spark.sql("CREATE DATABASE IF NOT EXISTS silver LOCATION '" + locationUri + "'");

        // Seed a real Silver Parquet dataset via the real SilverWriter —
        // the same class every *SilverJob writes through.
        StructType schema = new StructType()
                .add("customer_id", DataTypes.StringType, true)
                .add("customer_city", DataTypes.StringType, true)
                .add("customer_state", DataTypes.StringType, true);

        List<Row> rows = Arrays.asList(
                RowFactory.create("c001", "Sao Paulo", "SP"),
                RowFactory.create("c002", "Rio De Janeiro", "RJ")
        );

        Dataset<Row> silverDf = spark.createDataFrame(rows, schema);
        new SilverWriter().writeTable(silverDf, LakehouseTable.CUSTOMERS);
    }

    @Test
    void shouldRegisterSilverTableAndMakeItQueryable() {
        new HiveRegistrar().register(spark, HiveTable.SILVER_CUSTOMERS);

        List<Row> tables = spark.sql("SHOW TABLES IN silver").collectAsList();
        boolean customersTablePresent = tables.stream()
                .anyMatch(row -> "customers".equalsIgnoreCase(row.getAs("tableName")));

        assertTrue(customersTablePresent,
                "silver.customers should appear in SHOW TABLES IN silver after registration");

        Dataset<Row> queried = spark.sql("SELECT * FROM silver.customers ORDER BY customer_id");
        assertEquals(2, queried.count(),
                "Registered table should return the real Silver data, not an empty result");

        Row first = queried.first();
        assertEquals("c001", first.getAs("customer_id"));
        assertEquals("Sao Paulo", first.getAs("customer_city"));
        assertEquals("SP", first.getAs("customer_state"));
    }

    @Test
    void shouldBeIdempotentWhenRegisteredTwice() {
        // CREATE TABLE IF NOT EXISTS should make re-registration safe —
        // e.g. re-running a *SilverJob a second time shouldn't fail.
        HiveRegistrar registrar = new HiveRegistrar();

        assertDoesNotThrow(() -> {
            registrar.register(spark, HiveTable.SILVER_CUSTOMERS);
            registrar.register(spark, HiveTable.SILVER_CUSTOMERS);
        });

        long tableCount = spark.sql("SHOW TABLES IN silver")
                .filter("tableName = 'customers'")
                .count();

        assertEquals(1, tableCount, "Re-registering should not create a duplicate catalog entry");
    }

    @AfterAll
    static void teardown() {
        if (spark != null) {
            spark.stop();
        }
    }
}