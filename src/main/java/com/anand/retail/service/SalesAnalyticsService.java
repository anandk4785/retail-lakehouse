package com.anand.retail.service;

import com.anand.retail.constants.HiveTable;
import com.anand.retail.writer.HiveWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Builds the monthly product sales aggregate from the registered Silver
 * tables and persists it as a Gold managed table.
 *
 * Reads directly from the Hive catalog (silver.* tables already
 * registered by HiveRegistrar in each *SilverJob) rather than accepting
 * DataFrames as parameters. This mirrors SilverService's own shape
 * (ADR-014): the Service owns both I/O and aggregation logic, and the
 * Job class is pure DI wiring. The cost of this choice is testability —
 * buildMonthlyProductSales() cannot be unit-tested with small synthetic
 * DataFrames the way *TransformerTest/*ValidatorTest can, since it's
 * coupled to the Hive catalog by design. SalesAnalyticsServiceTest is
 * therefore a real integration test (real SparkSessionFactory session,
 * real Silver tables seeded and registered), the same style as
 * HiveRegistrarTest/SilverServiceTest, not a fast isolated unit test.
 */
public class SalesAnalyticsService implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(SalesAnalyticsService.class);

    private final HiveWriter writer;
    private final String targetDatabase;
    private final String targetTableName;

    public SalesAnalyticsService(HiveWriter writer, String targetDatabase, String targetTableName) {
        this.writer = writer;
        this.targetDatabase = targetDatabase;
        this.targetTableName = targetTableName;
    }

    public void run(SparkSession spark) {
        logger.info("--- Starting Sales Analytics Gold Execution Flow ---");
        long startTime = System.currentTimeMillis();

        Dataset<Row> monthlyProductSales = buildMonthlyProductSales(spark);
        long outputCount = monthlyProductSales.count();
        logger.info("Gold aggregate row count: {}", outputCount);

        writer.writeManagedTable(monthlyProductSales, targetDatabase, targetTableName, SaveMode.Overwrite);

        long endTime = System.currentTimeMillis();
        logger.info("--- Sales Analytics Gold Execution Completed in {} ms ---", endTime - startTime);
    }

    /**
     * Revenue is recognized only for delivered orders (orders.is_delivered,
     * computed once in OrderTransformer — see ADR-019 — reused here rather
     * than re-derived from order_status). Aggregate grain is
     * (purchase_year, purchase_month, product_category_name).
     */
    public Dataset<Row> buildMonthlyProductSales(SparkSession spark) {
        logger.info("Building monthly product sales from registered Silver Hive tables");

        String orderItems = HiveTable.SILVER_ORDER_ITEMS.getFullQualifiedName();
        String orders = HiveTable.SILVER_ORDERS.getFullQualifiedName();
        String products = HiveTable.SILVER_PRODUCTS.getFullQualifiedName();

        return spark.sql(
                "SELECT " +
                        "o.purchase_year, " +
                        "o.purchase_month, " +
                        "p.product_category_name, " +
                        "COUNT(*) AS item_count, " +
                        "SUM(oi.price) AS product_revenue, " +
                        "SUM(oi.freight_value) AS freight_revenue, " +
                        "SUM(oi.price + oi.freight_value) AS total_sales_amount, " +
                        "AVG(oi.price) AS average_item_price " +
                        "FROM " + orderItems + " oi " +
                        "INNER JOIN " + orders + " o ON oi.order_id = o.order_id " +
                        "INNER JOIN " + products + " p ON oi.product_id = p.product_id " +
                        "WHERE o.is_delivered = true " +
                        "GROUP BY o.purchase_year, o.purchase_month, p.product_category_name"
        );
    }
}