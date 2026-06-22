package com.anand.retail.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigLoaderTest {

    @Test

    void shouldReturnAppName() {

        String appName =

                ConfigLoader.get("app.name");

        assertEquals(

                "RetailLakehouse",

                appName

        );

    }

    @Test

    void shouldReturnSparkMaster() {

        String master =

                ConfigLoader.get(
                        "spark.master");

        assertEquals(

                "local[*]",

                master

        );

    }

    @Test

    void shouldThrowExceptionForMissingProperty() {

        assertThrows(

                IllegalArgumentException.class,

                () ->

                        ConfigLoader.get(

                                "wrong.property"

                        )

        );

    }

}