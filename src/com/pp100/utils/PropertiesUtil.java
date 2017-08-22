package com.pp100.utils;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesUtil {
    private static Properties prop;

    public static String getProperties(String key) {

        return getProperties(key, "");
    }

    public static String getProperties(String key, String def) {
        if (null == prop) {
            setProp();
        }

        return prop.getProperty(key, def);
    }

    private static void setProp() {
        if (prop == null) {
            try {
                prop = new Properties();
                prop.load(new FileInputStream("conf/config.conf"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
