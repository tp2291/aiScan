package com.cisco.wxcc.saa.integration;

import com.cisco.wxcc.saa.constants.AppConstants;
import com.cisco.wxcc.saa.helper.ConfigHelper;
import io.split.client.SplitClient;
import io.split.client.SplitClientConfig;
import io.split.client.SplitFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.apache.commons.configuration2.Configuration;

import java.util.Properties;

import static com.cisco.wxcc.saa.App.interactionId;
import static com.cisco.wxcc.saa.constants.AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME;

@Slf4j
public class FeatureFlagHelper {
    private static FeatureFlagHelper featureFlagHelper = null;
    private Configuration appProps;
    public SplitClient splitClient = null;
    private String vaultValue = null;


    public FeatureFlagHelper(){
        try {
            appProps = ConfigHelper.loadAppConfig();
        } catch (org.apache.commons.configuration2.ex.ConfigurationException e) {
            log.info("Unable to load configs : "+ e, Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId));
        }
        vaultValue = getSplitApiKeyFromVault();
    }

    public String getSplitApiKeyFromVault() {
        var credFilePath = appProps.getString("VAULT_SPLIT_FILE_PATH");
        try{
            Properties creds = ConfigHelper.loadFromFile(credFilePath);
            return creds.getProperty(AppConstants.SPLIT_VAULT_KEY);
        }catch (Exception ex0)
        {
            log.info("Getting value from split "+ex0, Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId));
            return  null;
        }
    }

    public static FeatureFlagHelper getInstance() {
        if (null == featureFlagHelper) {
            featureFlagHelper = new FeatureFlagHelper();
            featureFlagHelper.buildClient();
        }
        return featureFlagHelper;
    }

    private synchronized void buildClient() {
        if (null != splitClient) {
            return;
        }
        try {
            SplitClientConfig config = SplitClientConfig.builder()
                    .setBlockUntilReadyTimeout(10000)
                    .build();
            splitClient = SplitFactoryBuilder.build( vaultValue,config).client();
            splitClient.blockUntilReady();
            log.info("FeatureFlagHelper is ready to serve requests");

        } catch (Exception e) {
            log.error("Error while initializing FeatureFlagHelper "+e, Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId));
            if(null != splitClient){
                splitClient.destroy();
            }
            splitClient = null;
            Thread.currentThread().interrupt();
        }
    }

}
