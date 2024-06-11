package com.cisco.wxcc.saa.utils;

import com.cisco.wxcc.saa.exceptions.ConfigurationException;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfigHelper {

    private ConfigHelper(){}
    private static String propertyFileName = "app.properties";

    public static Properties loadFromFile(String configFile) throws ConfigurationException {
        Properties prop = new Properties();

        try (InputStream is = new FileInputStream(configFile)) {
            prop.load(is);
        } catch (Exception ex) {
            throw new ConfigurationException(ex.getMessage());
        }

        return prop;
    }

    public static Configuration loadAppConfig() throws org.apache.commons.configuration2.ex.ConfigurationException {
        Configurations configs = new Configurations();
        Configuration config;
        try {
            config = configs.properties(ConfigHelper.class.getClassLoader().getResource(propertyFileName));
        }
        catch (org.apache.commons.configuration2.ex.ConfigurationException cex) {
            throw new org.apache.commons.configuration2.ex.ConfigurationException(cex);
        }
        return config;
    }


}
