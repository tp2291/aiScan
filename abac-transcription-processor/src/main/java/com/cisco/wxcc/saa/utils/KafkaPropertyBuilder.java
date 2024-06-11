package com.cisco.wxcc.saa.utils;

import com.cisco.wxcc.saa.constants.AppConstants;
import com.cisco.wxcc.saa.exceptions.ConfigurationException;
import com.jasongoodwin.monads.Try;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.apache.commons.configuration2.Configuration;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

import static com.cisco.wxcc.saa.App.interactionId;
import static com.cisco.wxcc.saa.App.orgId;
import static com.cisco.wxcc.saa.constants.AppConstants.*;

@Slf4j
public class KafkaPropertyBuilder {

    private KafkaPropertyBuilder(){}
    private static Configuration appProps;

    static {
        try {
            appProps = ConfigHelper.loadAppConfig();
        } catch (org.apache.commons.configuration2.ex.ConfigurationException e) {
            log.info("Unable to load configs : "+ e, Markers.append(KIBANA_INTERACTION_ID_FIELD_NAME, interactionId), Markers.append(KIBANA_ORG_ID_FIELD_NAME, orgId));
        }
    }

    public static Properties loadConsumerConfig() {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, appProps.getString("CONSUMER_BROKER"));
        loadCommonConsumerConfig(kafkaProps);
        return kafkaProps;
    }

    public static Properties buildFromConfluentConfig(String filePath) throws ConfigurationException {
        Properties kafkaProps = ConfigHelper.loadFromFile(filePath);
        loadCommonConsumerConfig(kafkaProps);
        return kafkaProps;
    }

    private static void loadCommonConsumerConfig(Properties props)
    {
        // Add additional properties.
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, AppConstants.KEY_DESERIALIZER_CLASS_CONFIG);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AppConstants.VALUE_DESERIALIZER_CLASS_CONFIG);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, appProps.getString("GROUP_ID_CONFIG"));
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, AppConstants.AUTO_OFFSET_RESET_CONFIG);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, AppConstants.MAX_POLL_RECORDS_CONFIG);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, AppConstants.MAX_POLL_INTERVAL_MS_CONFIG);
    }

    public static Properties loadProducerConfig() {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, appProps.getString("PRODUCER_BROKER"));
        kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProps.put(ProducerConfig.ACKS_CONFIG, "all");
        return kafkaProps;
    }
}
