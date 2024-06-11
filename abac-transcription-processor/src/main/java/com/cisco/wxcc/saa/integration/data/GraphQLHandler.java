package com.cisco.wxcc.saa.integration.data;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

@Slf4j
public class GraphQLHandler {

    public static JSONObject getCSRData(Configuration appConfig,
                                  String interactionId, String orgId,
                                  String accessToken) {
        JSONObject responseJson = null;
        log.info(String.format("Getting agent Id for the interaction Id: %s", interactionId));

        try {
            URL url = new URL(appConfig.getString("DAL_SEARCH"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("X-ORGANIZATION-ID", orgId);
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setDoOutput(true);

            long now = Instant.now().toEpochMilli();
            //Check for last 24 hours
            Long from_date = Instant.ofEpochMilli(now).minus(Duration.ofSeconds(24 * 60 * 60)).toEpochMilli();
            Long to_date = Long.valueOf(now);

            String query = "{taskDetails(" +
                    "    from: " + from_date +
                    "    to: " + to_date +
                    "    filter: { and: [{ isActive: { equals: false } }, { id: { equals: \\\"" + interactionId + "\\\" } }] }" +
                    "    pagination: { cursor: \\\"0\\\" }" +
                    "  ) {" +
                    "    tasks {" +
                    "      id" +
                    "      createdTime" +
                    "      endedTime" +
                    "      direction" +
                    "      contactReason" +
                    "      queueDuration" +
                    "      ringingDuration" +
                    "      selfserviceDuration" +
                    "      holdCount" +
                    "      holdDuration" +
                    "      conferenceCount" +
                    "      connectedDuration" +
                    "      transferCount" +
                    "      blindTransferCount" +
                    "      lastWrapUpCodeId" +
                    "      wrapupDuration" +
                    "      lastAgent{" +
                    "        id" +
                    "        sessionId" +
                    "      }" +
                    "    }" +
                    "    pageInfo {" +
                    "      hasNextPage" +
                    "      endCursor" +
                    "    }" +
                    "  }" +
                    "}";
            String payload = "{\"query\": \"" + query + "\"}";

            OutputStream os = conn.getOutputStream();
            os.write(payload.getBytes());
            os.flush();

            int statusCode = conn.getResponseCode();
            String response = conn.getResponseMessage();

            BufferedReader br = new BufferedReader(new InputStreamReader((statusCode == 200) ? conn.getInputStream() : conn.getErrorStream()));
            String output;
            while ((output = br.readLine()) != null) {
                responseJson = new JSONObject(output);
            }

            conn.disconnect();
        } catch (Exception e) {
            log.error(String.format("Unable to fetch agent id for the interaction Id: %s", interactionId));
        }

        return responseJson;
    }

    public static String getAgentId(JSONObject responseJson, String interactionId) {

        String agentId = "";
        JSONArray tasksArray = responseJson.getJSONObject("data")
                .getJSONObject("taskDetails")
                .getJSONArray("tasks");

        if (tasksArray.length() > 0) {
            JSONObject lastAgent;
            if (tasksArray.getJSONObject(0).isNull("lastAgent")) {
                lastAgent = null;
            } else {
                lastAgent = tasksArray
                        .getJSONObject(0)
                        .getJSONObject("lastAgent");
            }

            if (lastAgent != null) {
                if (lastAgent.isNull("id")) {
                    log.info("lastAgent.id is null for interaction id: " + interactionId);
                } else {
                    agentId = lastAgent
                            .getString("id");
                }
            } else {
                log.info("lastAgent is null for interaction id: " + interactionId);
            }

            log.info(String.format("The agent Id is %s for the interaction Id: %s", agentId, interactionId));
        }
        return agentId;
    }

    public static JSONObject getCSRObject(JSONObject responseJson, String orgId) {

        JSONObject csrObject = null;
        JSONArray tasksArray = responseJson.getJSONObject("data")
                .getJSONObject("taskDetails")
                .getJSONArray("tasks");

        if (tasksArray.length() > 0) {
            csrObject = tasksArray.getJSONObject(0);
            csrObject.put("orgId", orgId);
        }
        return csrObject;
    }
}
