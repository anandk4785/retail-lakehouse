package com.anand.retail.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigLoader {

    private static final Properties PROPERTIES = new Properties();

    static {

        try (InputStream inputStream =

                     ConfigLoader.class
                             .getClassLoader()
                             .getResourceAsStream(
                                     "application.properties")) {

            if (inputStream == null) {

                throw new RuntimeException(
                        "application.properties not found");

            }

            PROPERTIES.load(inputStream);

        }

        catch (IOException e) {

            throw new RuntimeException(
                    "Failed to load application.properties",
                    e);

        }

    }

    private ConfigLoader() {

    }

    public static String get(String key) {

        String value =

                PROPERTIES.getProperty(key);

        if (value == null) {

            throw new IllegalArgumentException(

                    "Missing property : "

                            + key);

        }

        return value;

    }

}