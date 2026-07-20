package com.anand.retail.transform;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import static org.apache.spark.sql.functions.coalesce;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.lit;

public class PaymentTransformer implements DataTransformer, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(PaymentTransformer.class);

    @Override
    public Dataset<Row> transform(Dataset<Row> df) {
        logger.info("Applying transformations for Payment Fact");
        return df
                // Payment Flags
                .withColumn(
                        "is_installment_payment",
                        coalesce(col("payment_installments").gt(1), lit(false))
                )

                .withColumn(
                        "is_credit_card",
                        col("payment_type").equalTo("credit_card")
                )

                // Rename payment amount for clarity
                .withColumnRenamed(
                        "payment_value",
                        "payment_amount"
                )

                // Shape final Silver dataset
                .select(
                        col("order_id"),

                        col("payment_sequential"),

                        col("payment_type"),

                        col("payment_installments"),

                        col("payment_amount"),

                        col("is_installment_payment"),

                        col("is_credit_card")
                );
    }
}
