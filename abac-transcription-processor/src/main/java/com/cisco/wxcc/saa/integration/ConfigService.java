package com.cisco.wxcc.saa.integration;

import com.cisco.wxcc.saa.utils.ConfigHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Slf4j
public class ConfigService {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static Configuration appProps;

    static {
        try {
            appProps = ConfigHelper.loadAppConfig();
        } catch (org.apache.commons.configuration2.ex.ConfigurationException e) {
            log.info("Unable to load configs : "+ e);
        }
    }

    public static String getOrgIdFromTenantId(String tenantId, String accessToken) throws IOException, InterruptedException {

        String url = String.format("%s/cms/api/organization?filter=tenantId==%s",
                appProps.getString("CONFIG_SERVICE_URL"), tenantId);

        HttpRequest codeRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = client.send(codeRequest, HttpResponse.BodyHandlers.ofString());
        JSONArray responseArray = new JSONArray(response.body());
        JSONObject responseJson = responseArray.getJSONObject(0);

        return responseJson.getString("organizationId");
    }
}
