package com.anand.retail.validator;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;

public class NullPkDedupValidator implements DataValidator, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(NullPkDedupValidator.class);

    private final String[] requiredColumns;
    private final String[] deduplicateColumns;

    public NullPkDedupValidator(
            String[] requiredColumns,
            String[] deduplicateColumns
    ) {
        this.requiredColumns = requiredColumns;
        this.deduplicateColumns = deduplicateColumns;
    }

    @Override
    public Dataset<Row> validate(Dataset<Row> df) {
        logger.info("Validating data: Removing null [{}] and duplicates [{}]",
                Arrays.toString(requiredColumns), Arrays.toString(deduplicateColumns));

        Dataset<Row> validDf = df;
        for (String column : requiredColumns) {
            validDf = validDf.filter(validDf.col(column).isNotNull());
        }
        return validDf.dropDuplicates(deduplicateColumns);
    }
}
