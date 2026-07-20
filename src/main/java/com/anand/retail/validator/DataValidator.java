package com.anand.retail.validator;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.Serializable;

public interface DataValidator extends Serializable {

    Dataset<Row> validate(Dataset<Row> df);
}
