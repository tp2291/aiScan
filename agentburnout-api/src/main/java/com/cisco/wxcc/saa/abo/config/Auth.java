package com.cisco.wxcc.saa.abo.config;


import com.cisco.codev.identity.sdk.validator.InitParams;
import com.cisco.codev.identity.sdk.validator.OAuth2TokenValidator;
import com.cisco.codev.identity.sdk.validator.TokenValidationResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static com.cisco.wxcc.saa.abo.constants.AppConstants.*;

@Slf4j
public class Auth {
    private static Auth authInstance;
    private final Set<String> roles = new HashSet<>(List.of(AUTHORIZED_ROLES));
    private Properties props = new Properties();
    private OAuth2TokenValidator validator = new OAuth2TokenValidator();
    private String OAUTH_PROPERTIES_FILENAME = System.getenv("oauth_properties_file");
    @SneakyThrows
    private Auth() {

        var inputStream = Auth.class.getClassLoader().getResourceAsStream(OAUTH_PROPERTIES_FILENAME);
        props.load(inputStream);
        InitParams params = InitParams.getInstance(props);
        validator.setConfig(params);
    }



    public static Auth getInstance() {
        if (authInstance == null) {
            authInstance = new Auth();
        }
        return authInstance;
    }

    @SneakyThrows
    public boolean isInValidToken(String token) {
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            return true;
        }
        token = token.substring(7);
        TokenValidationResult result = validator.validate(token, roles);
        return !result.isSuccess();
    }

    public String getOrgId(String token) {
        token = token.substring(7);
        TokenValidationResult result = validator.validate(token, roles);
        return result.getRealm();
    }

    public String getOwnerId(String token) {
        token = token.substring(7);
        TokenValidationResult result = validator.validate(token, roles);
        return result.getUserID();
    }

    public String getRole(String token){
        return "Supervisor";
    }
}
