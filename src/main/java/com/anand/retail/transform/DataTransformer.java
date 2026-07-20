package com.anand.retail.transform;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.Serializable;

public interface DataTransformer extends Serializable {

    Dataset<Row> transform(Dataset<Row> df);
}
