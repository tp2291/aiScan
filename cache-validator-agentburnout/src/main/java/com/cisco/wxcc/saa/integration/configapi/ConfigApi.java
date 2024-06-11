package com.cisco.wxcc.saa.integration.configapi;

import com.cisco.wxcc.saa.helper.ConfigHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ConfigApi {


    private final HttpClient httpClient = HttpClient.newHttpClient();


    private static Configuration appProps;

    static {
        try {
            appProps = ConfigHelper.loadAppConfig();
        } catch (org.apache.commons.configuration2.ex.ConfigurationException e) {

        }
    }

    public List<String> getConfig(String orgId, String teamId, String accessToken) throws IOException, InterruptedException {

        List<String> list = new ArrayList<String>();

        String url = String.format("%s/cms/api/organization/%s/team/%s",
                appProps.getString("CONFIG_SERVICE_URL"), orgId,teamId);

        HttpRequest codeRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = this.httpClient.send(codeRequest, HttpResponse.BodyHandlers.ofString());
        try{
            if (response.statusCode()==200){
                JSONObject responseJson = new JSONObject(response.body());

                // Check if the response JSON object has the key "userIds"
                if (responseJson.has("userIds")) {
                    JSONArray userIds = responseJson.getJSONArray("userIds");
                    if (userIds != null) {
                        int len = userIds.length();
                        for (int i = 0; i < len; i++) {
                            list.add(userIds.getString(i));
                        }
                    }
                } else {
                    log.warn("The 'userIds' key is missing in the JSON response for orgId: " + orgId + " and teamId: " + teamId );
                }

            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            log.error("Failed to parse JSON.");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("An error occurred.");
        }
        return (list.isEmpty()) ? null : list;
    }







    public List<String> getAgents(String orgId, String accessToken) throws IOException, InterruptedException {

        List<String> agentIdList = new ArrayList<>();
        String url = String.format("%s/agentburnout/onboarding?orgId=%s",
                appProps.getString("DB_API_URL"), orgId);

        HttpRequest codeRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = this.httpClient.send(codeRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode()==200){
            try {
                JSONObject responseJson = new JSONObject(response.body());
                JSONArray agentIdsJsonArray = responseJson.getJSONArray("agentIds");

                for(int i = 0; i < agentIdsJsonArray.length(); i++){
                    agentIdList.add(agentIdsJsonArray.getString(i));
                }

                JSONArray teamIdsJsonArray = responseJson.getJSONArray("teamIds");
                List<String> agent_teamIds = new ArrayList<>();
                for(int i = 0; i < teamIdsJsonArray.length(); i++){
                    List<String> result=getConfig(orgId,teamIdsJsonArray.getString(i),accessToken);
                    if (result != null && !result.isEmpty()){
                        agent_teamIds.addAll(result);
                    }



                }
                if (!agent_teamIds.isEmpty()){
                    agentIdList.addAll(agent_teamIds);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                log.error("Failed to parse JSON.");
            } catch (Exception e) {
                e.printStackTrace();
                log.error("An error occurred.");
            }
            return agentIdList;

        }







        return agentIdList;
    }
}
