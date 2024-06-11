package com.cisco.wxcc.saa.helper;

import com.cisco.wxcc.saa.constants.AppConstants;
import com.cisco.wxcc.saa.exceptions.ConfigurationException;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigHelper {


    private static final String PROPERTY_FILE_NAME = "app.properties";

    public static Properties loadFromFile(String configFile) throws ConfigurationException, IOException {
        Properties prop = new Properties();

        InputStream is = null;

        try {
            is = new FileInputStream(configFile);
            prop.load(is);

        } catch (Exception ex) {
            throw new ConfigurationException(ex.getMessage());
        }
        finally {
            if(is != null){
                is.close();
            }
        }

        return prop;
    }

    public static Configuration loadAppConfig() throws org.apache.commons.configuration2.ex.ConfigurationException {
        Configurations configs = new Configurations();
        Configuration config;
        try {
            config = configs.properties(ConfigHelper.class.getClassLoader().getResource(PROPERTY_FILE_NAME));
        }
        catch (org.apache.commons.configuration2.ex.ConfigurationException cex) {
            throw new org.apache.commons.configuration2.ex.ConfigurationException(cex);
        }
        return config;
    }

    public static Properties getDBProperties() throws ConfigurationException, org.apache.commons.configuration2.ex.ConfigurationException, IOException {
        Configuration appProps = ConfigHelper.loadAppConfig();
        Properties props = new Properties();
        String credFilePath = appProps.getString(AppConstants.VAULT_CRED_FILE_PATH);
        Properties creds = ConfigHelper.loadFromFile(credFilePath);
        props.put(AppConstants.USERNAME, creds.getProperty(AppConstants.USERNAME));
        props.put(AppConstants.PASSWORD, creds.getProperty(AppConstants.PASSWORD));
        return props;
    }
}
