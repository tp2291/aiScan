package com.cisco.wxcc.saa;

import com.cisco.wxcc.saa.exceptions.AuthClientException;
import com.cisco.wxcc.saa.integration.ConfigService;
import com.cisco.wxcc.saa.integration.auth.AuthTokenManager;
import com.cisco.wxcc.saa.integration.data.GraphQLHandler;
import com.cisco.wxcc.saa.integration.redis.RedisHelper;
import com.cisco.wxcc.saa.utils.ConfigHelper;
import com.cisco.wxcc.saa.utils.HttpServerHelper;
import com.cisco.wxcc.saa.utils.KafkaPropertyBuilder;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.Counter;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Properties;

import static com.cisco.wxcc.saa.constants.AppConstants.*;

@Slf4j
public class App {

    public static String interactionId = "";
    public static String orgId = "";
    public static String agentId = "";

    public static void main(String[] args) throws ConfigurationException, com.cisco.wxcc.saa.exceptions.ConfigurationException, IOException, InterruptedException, AuthClientException {

        Properties kafkaProps;
        String inputKafkaTopic;
        final Configuration appProps = ConfigHelper.loadAppConfig();
        String agentBurnoutRedisSet = appProps.getString("AGENTBURNOUT_ENABLED_AGENTS_REDIS_SET");
        String autoCsatRedisSet = appProps.getString("AUTOCSAT_ENABLED_AGENTS_REDIS_SET");
        String tenantOrgRedisHash = appProps.getString("TENANT_ORG_REDIS_HASH");
        String producerKafkaTopic;
        AuthTokenManager authTokenManager;

        boolean runInTahoe = (args.length == 0);

        if (runInTahoe)
        {
            log.info("Running with data-platform configuration", Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId), Markers.append(KIBANA_AGENT_ID_FIELD_NAME, agentId), Markers.append(KIBANA_ORG_ID_FIELD_NAME, orgId));

            authTokenManager = new AuthTokenManager();
            kafkaProps = KafkaPropertyBuilder.loadConsumerConfig();
            inputKafkaTopic = appProps.getString("WXCC_INPUT_KAFKA_TOPIC");
            producerKafkaTopic = appProps.getString("PRODUCER_KAFKA_TOPIC");
        }
        else {
            if (args[0].isBlank())
                throw new RuntimeException("Confluent config file or insight auth config file not provided.");

            log.info("Running with dev configuration", Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId), Markers.append(KIBANA_AGENT_ID_FIELD_NAME, agentId), Markers.append(KIBANA_ORG_ID_FIELD_NAME, orgId));

            authTokenManager = new AuthTokenManager(args[1]);
            kafkaProps = KafkaPropertyBuilder.buildFromConfluentConfig(args[0]);
            inputKafkaTopic = appProps.getString("CONFLUENT_INPUT_KAFKA_TOPIC");
            producerKafkaTopic = appProps.getString("CONFLUENT_OUTPUT_KAFKA_TOPIC");
        }

        HttpServerHelper.startServer(Integer.parseInt(appProps.getString("METRIC_ENDPOINT_PORT")));
        PrometheusMeterRegistry registry = HttpServerHelper.getRegistry();

        Counter nrtEventsProduced = Counter.build()
                .name("SAA_ABACTP_EVENTS_PRODUCED")
                .labelNames("orgId")
                .help("Number of times transcription-needed event was produced.")
                .register(registry.getPrometheusRegistry());

        Counter exceptionsCount= Counter.build()
                .name("SAA_ABACTP_EXCEPTIONS_COUNT")
                .labelNames("orgId")
                .help("Number of times exceptions were encountered")
                .register(registry.getPrometheusRegistry());

        RedisHelper redis = new RedisHelper();

        Properties kafkaProducerProps = KafkaPropertyBuilder.loadProducerConfig();
        final KafkaProducer<String, String> producer = new KafkaProducer<>(kafkaProducerProps);
        final Consumer<String, String> consumer = new KafkaConsumer<>(kafkaProps);

        try (consumer) {
            consumer.subscribe(Collections.singletonList(inputKafkaTopic));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(2));
                for (ConsumerRecord<String, String> consumerRecord : records) {
                    try {
                        String key = consumerRecord.key();
                        JSONObject value = new JSONObject(consumerRecord.value());
                        String eventName = value.get("eventName").toString();

                        if (RECORDING_AVAILABLE_EVENT.equalsIgnoreCase(eventName)) {

                            String tenantId = value.get("path").toString().split("/")[0];
                            interactionId = value.get("entityId").toString();
                            String accessToken = "";

                            if (runInTahoe) {
                                accessToken = authTokenManager.getTokenFromVault();
                            } else {
                                accessToken = authTokenManager.getTokenFromFile(args[1]);
                            }

                            if (redis.hexists(tenantOrgRedisHash, tenantId)) {
                                log.info("Present in redis tenantId: " + tenantId, Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId), Markers.append(KIBANA_AGENT_ID_FIELD_NAME, agentId));
                                orgId = redis.hget(tenantOrgRedisHash, tenantId);
                            } else {
                                log.info("Getting orgId from config service for tenantId: " + tenantId, Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId), Markers.append(KIBANA_AGENT_ID_FIELD_NAME, agentId));
                                orgId = ConfigService.getOrgIdFromTenantId(tenantId, accessToken);
                                redis.hset(tenantOrgRedisHash, tenantId, orgId);
                            }
                            log.info("Org id for tenantId " + tenantId + " is: " + orgId, Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId), Markers.append(KIBANA_AGENT_ID_FIELD_NAME, agentId));

                            JSONObject graphqlJson = null;

                            for (int i = 1; i <= 3; i++) {
                                graphqlJson = GraphQLHandler.getCSRData(appProps,
                                        interactionId,
                                        orgId,
                                        accessToken);
                                if(graphqlJson != null){
                                    agentId = GraphQLHandler.getAgentId(graphqlJson, interactionId);
                                    if (!"".equalsIgnoreCase(agentId)) {
                                        break;
                                    }
                                }

                                log.info("Unable to get csr data retrying after 30 seconds. Attempt: " + i);
                                Thread.sleep(30000);
                            }

                            if ("".equalsIgnoreCase(agentId)) {
                                log.info(String.format("Skipping event, agentId is null for the interaction Id: %s", interactionId));
                                continue;
                            }

                            if (redis.sismember(agentBurnoutRedisSet, agentId) ||
                                    redis.sismember(autoCsatRedisSet, agentId)) {

                                log.info(String.format("Feature is enabled for InteractionId %s, producing event at time %s", interactionId, Instant.now().toEpochMilli()), Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId), Markers.append(KIBANA_AGENT_ID_FIELD_NAME, agentId), Markers.append(KIBANA_ORG_ID_FIELD_NAME, orgId));

                                JSONObject csrObject = GraphQLHandler.getCSRObject(graphqlJson, orgId);

                                log.info(String.format("Saving CSR data for interactionId: %s and agentId: %s in redis",interactionId ,agentId), Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId), Markers.append(KIBANA_AGENT_ID_FIELD_NAME, agentId));
                                redis.setex(REDIS_ABAC_CSR_PREFIX + interactionId, REDIS_TTL_IN_SECONDS,  csrObject.toString());

                                // produce event
                                JSONObject payload = new JSONObject();
                                payload.put("interactionId", interactionId);
                                payload.put("orgId", orgId);
                                String jsonValue = payload.toString();

                                final String currentInteractionId = interactionId;
                                producer.send(new ProducerRecord<>(producerKafkaTopic, jsonValue), (m, e) -> {
                                    if (e != null) {
                                        e.printStackTrace();
                                        log.error("Failed to send transcription-needed for interactionId " + interactionId, e);
                                    } else {
                                        log.info(String.format("Published transcription-needed event at time %s with value as %s", Instant.now().toEpochMilli(), jsonValue),
                                                Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, currentInteractionId), Markers.append(KIBANA_AGENT_ID_FIELD_NAME, agentId), Markers.append(KIBANA_ORG_ID_FIELD_NAME, orgId));
                                        nrtEventsProduced.labels(orgId).inc();
                                    }
                                });
                            }
                        }

                    } catch (Exception e) {
                        log.error("Exception caught: " + e, Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId), Markers.append(KIBANA_AGENT_ID_FIELD_NAME, agentId), Markers.append(KIBANA_ORG_ID_FIELD_NAME, orgId));
                        if (orgId.equals("")) {
                            exceptionsCount.labels("N/A").inc();
                        } else {
                            exceptionsCount.labels(orgId).inc();
                        }
                    } finally {
                        interactionId = "";
                        orgId = "";
                        agentId = "";
                    }
                }
            }
        }

    }

}
