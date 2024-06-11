package com.cisco.wxcc.saa;

import java.sql.*;
import java.util.*;

import com.cisco.wxcc.saa.constants.AppConstants;
import com.cisco.wxcc.saa.helper.AuthTokenHelper;
import com.cisco.wxcc.saa.helper.ConfigHelper;
import com.cisco.wxcc.saa.integration.FeatureFlagHelper;
import com.cisco.wxcc.saa.integration.configapi.ConfigApi;
import com.cisco.wxcc.saa.integration.redis.RedisHelper;
import io.split.client.SplitClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.json.JSONArray;

@Slf4j
public class App {

    public static String interactionId = "";

    public static String orgId = "";

    public static void main(String[] args) throws Exception {
        // initialise cache helper
        //update the redis cache
        List<String> agentIdsList = new ArrayList<>();
        List<String> teamIdsList = new ArrayList<>();
        log.info("Agentbunout Config validator cron job started!");

        AuthTokenHelper authHelper = new AuthTokenHelper();
        RedisHelper redis = new RedisHelper();

        ConfigApi config = new ConfigApi();
        var authToken = authHelper.getTokenFromVault();
        //Load configurations
        Configuration appProps = ConfigHelper.loadAppConfig();
        FeatureFlagHelper featureFlagHelper = FeatureFlagHelper.getInstance();
        SplitClient splitClint = featureFlagHelper.splitClient;

        Properties dbProps = ConfigHelper.getDBProperties();
        String url = String.format("jdbc:postgresql://%s:%s/%s",
                appProps.getString("DB_HOST"),
                appProps.getString("DB_PORT"),
                appProps.getString("DB_NAME"));

        String user = dbProps.getProperty(AppConstants.USERNAME);
        String password = dbProps.getProperty(AppConstants.PASSWORD);

        //connect to db
        log.info("Connecting to database...");
        Connection conn = DriverManager.getConnection(url, user, password);

        //sql query to get the list of all the agentIds
        PreparedStatement agent_stmt = conn.prepareStatement("SELECT org_id FROM ab.config;");
        ResultSet agent_rs = agent_stmt.executeQuery();

        while (agent_rs.next()) {
            String orgId= agent_rs.getString("org_id");
            log.info("OrgId is" + orgId);

            String treatmentAgentBurnout = splitClint.getTreatment(orgId,
                    appProps.getString("SPLIT_NAME_AGENT_BURNOUT"));

            boolean canProcesses = treatmentAgentBurnout.equals("on");

            log.info("Split treatment for org id:" + orgId + " is " + treatmentAgentBurnout);


            if (!canProcesses) {
                log.info("Skipping redis addition as split treatment for org id:" + orgId + " is off");
                continue;
            }
            List<String> agentIds = config.getAgents(orgId, authToken);
            Set<String> agentIdsSet = new HashSet<>(agentIds);

            //Add agentIds to redis cache list
            agentIdsList.addAll(agentIds);

            //sql query to get the list of all the agentIds which has stored models for the org
            PreparedStatement models_stmt = conn.prepareStatement("SELECT agent_id FROM ab.model WHERE org_id=?;");
            models_stmt.setString(1, orgId);
            ResultSet models_rs = models_stmt.executeQuery();
            List<String> modelAgentIds = new ArrayList<>();

            while (models_rs.next()) {
                String agentId= models_rs.getString("agent_id");
                modelAgentIds.add(agentId);
            }
            models_rs.close();
            models_stmt.close();

            //Insert blank records for the agentIds whose record does not exist in model table
            // Create a new ArrayList from agentIds
            List<String> nonModelAgentIds = new ArrayList<>(agentIdsSet);

            // Remove all elements in modelAgentIds from nonModelAgentIds
            nonModelAgentIds.removeAll(modelAgentIds);

            //Insert blank records for these agentIds
            for(String agentId : nonModelAgentIds){
                String query = "INSERT INTO ab.model (org_id, agent_id, status) VALUES (?, ?, ?)";
                try (PreparedStatement insertBlank = conn.prepareStatement(query)) {
                    insertBlank.setString(1, orgId);
                    insertBlank.setString(2, agentId);
                    insertBlank.setInt(3,  AppConstants.MODEL_STATUS_INSUFFICIENT_DATA);
                    insertBlank.executeUpdate();

                    log.info("Inserting empty record in database for orgId: " + orgId + " agentId: " + agentId);
                } catch (SQLException ex) {
                    log.error("Unable to insert blank record to db due to "+ex.getMessage()+" for orgId : "+orgId+" and agentId : "+ agentId);
                }
            }

        }

        agent_rs.close();
        agent_stmt.close();

        conn.close();


        //connect to cache and update

        log.info("deleting cache");
        redis.del(AppConstants.REDIS_SET_NAME);
        if (!agentIdsList.isEmpty()) {;
            log.info("Updating cache");
            redis.sadd(AppConstants.REDIS_SET_NAME, agentIdsList.toArray(new String[0]));
        }
        else{
            log.info("agentids list empty");
        }


        //Holding app exit for 10 minutes to update logs in Kibana
        Thread.sleep(600000);
    }

}

