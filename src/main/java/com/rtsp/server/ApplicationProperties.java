package com.rtsp.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {

    private static final String FILE_NAME = "Application.properties";
    private static ApplicationProperties applicationProperties;
    private Properties properties;

    private ApplicationProperties() {
        properties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream(FILE_NAME);
        try {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getAppProperty(String key) {
        return (String) properties.get(key);
    }

    public static String getProperty(String key) {
        String envVar = System.getenv(key);
        if (envVar != null) {
            return envVar;
        }
        var property = getApplicationProperties().getAppProperty(key);
        if (property == null) {
            return "";
        }
        return property;
    }

    private static ApplicationProperties getApplicationProperties() {
        if (applicationProperties == null) {
            applicationProperties = new ApplicationProperties();
        }
        return applicationProperties;
    }
}
