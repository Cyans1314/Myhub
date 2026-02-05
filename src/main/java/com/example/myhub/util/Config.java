package com.example.myhub.util;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Configuration utility for loading properties
 * ============================================================================
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties props = new Properties();

    static {
        try {
            InputStream is = Config.class.getClassLoader()
                    .getResourceAsStream("db.properties");
            if (is != null) {
                props.load(is);
                is.close();
            }

            InputStream is2 = Config.class.getClassLoader()
                    .getResourceAsStream("app.properties");
            if (is2 != null) {
                props.load(is2);
                is2.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}