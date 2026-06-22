package com.anand.retail.factory;

import org.apache.spark.sql.SparkSession;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SparkSessionFactoryTest {
    @Test

    void shouldCreateSparkSession() {

        SparkSession spark =

                SparkSessionFactory
                        .getSparkSession();

        assertNotNull(spark);

    }

    @Test

    void shouldReturnSameSparkSession() {

        SparkSession s1 =

                SparkSessionFactory
                        .getSparkSession();

        SparkSession s2 =

                SparkSessionFactory
                        .getSparkSession();


        assertSame(

                s1,

                s2

        );

    }
}
