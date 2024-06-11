package com.cisco.wxcc.saa.integration.auth;

import com.cisco.wxcc.saa.constants.AppConstants;
import com.cisco.wxcc.saa.exceptions.AuthClientException;
import com.cisco.wxcc.saa.exceptions.ConfigurationException;
import com.cisco.wxcc.saa.pojo.OAuthTokenInfo;
import com.cisco.wxcc.saa.utils.ConfigHelper;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.apache.commons.configuration2.Configuration;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import static com.cisco.wxcc.saa.App.agentId;
import static com.cisco.wxcc.saa.App.interactionId;
import static com.cisco.wxcc.saa.constants.AppConstants.KIBANA_AGENT_ID_FIELD_NAME;
import static com.cisco.wxcc.saa.constants.AppConstants.KIBANA_INTERACTION_ID_FIELD_NAME;

@Slf4j
public class AuthTokenManager {

    public AuthTokenManager() throws ConfigurationException, AuthClientException, IOException, InterruptedException {
        getTokenFromVault();
    }

    public AuthTokenManager(String credFile) throws ConfigurationException, AuthClientException, IOException, InterruptedException {
        getTokenFromFile(credFile);
    }

    private static Configuration appProps;
    static OAuthTokenHandler oAuth2TokenHelper;

    static {
        try {
            appProps = ConfigHelper.loadAppConfig();
            oAuth2TokenHelper = new OAuthTokenHandler();
        } catch (org.apache.commons.configuration2.ex.ConfigurationException e) {
            log.info("Unable to load configs : "+ e, Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId), Markers.append(KIBANA_AGENT_ID_FIELD_NAME, agentId));
        }
    }

    public String getTokenFromVault() throws ConfigurationException, IOException, InterruptedException, AuthClientException {
        String credFilePath = appProps.getString("VAULT_CRED_FILE_PATH");
        Properties creds = ConfigHelper.loadFromFile(credFilePath);
        OAuthTokenInfo tokenInfo = getOAuthTokenInfo(creds, appProps.getString("SCOPE_CJP_READ"));
        return tokenInfo.getAccessToken();
    }

    public String getTokenFromFile(String credFile) throws ConfigurationException, IOException, InterruptedException, AuthClientException {
        Properties props = ConfigHelper.loadFromFile(credFile);
        OAuthTokenInfo tokenInfo = getOAuthTokenInfo(props, appProps.getString("SCOPE_CJP_READ"));
        return tokenInfo.getAccessToken();
    }

    public OAuthTokenInfo getOAuthTokenInfo(Properties props, String scope) throws IOException, InterruptedException, AuthClientException {
        String name = props.getProperty(AppConstants.NAME);
        String password = props.getProperty(AppConstants.PASSWORD);
        String orgId = props.getProperty(AppConstants.ORG_ID);
        Optional<OAuthTokenInfo> tokenInfo = oAuth2TokenHelper.getAccessToken(name, password, orgId, scope);
        return tokenInfo.orElseThrow(
                () -> new AuthClientException("Auth Token not present!"));
    }
}
