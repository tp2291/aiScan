package com.cisco.wxcc.saa.abo.constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppConstants {


    public static final String AUTHORIZED_ROLES = "cjp:config_read";

    public static final String BEARER_PREFIX = "Bearer ";

    public static final String MOCK_EVENT_TOPIC_NAME = "agentburnout.index";

    public static final String CATALOGUE_URL = String.format("https://catalog.%s.ciscoccservice.com/catalog/registration/agentburnout/index", System.getenv("DC"));

    public static final String KAFKA_URL_JSON_PATH = "/dataPlaneConfig/kafkaUrl";

    public String NOTIFS_REGISTER_URL= System.getenv("NOTIFS_REGISTER_URL");


    public String SWAGGER_API_URL="https://config-service.%s.ciscoccservice.com//cms/api/organization/%s/team/%s";


    public String getNotifsRegisterUrl() {
        return NOTIFS_REGISTER_URL;
    }

    public void setNotifsRegisterUrl(String notifsRegisterUrl) {
        NOTIFS_REGISTER_URL = notifsRegisterUrl;
    }

    public String getSwaggerApiUrl() {
        return SWAGGER_API_URL;
    }

    public void setSwaggerApiUrl(String swaggerApiUrl) {
        SWAGGER_API_URL = swaggerApiUrl;
    }

    @Autowired
    public AppConstants() {
    }

}
