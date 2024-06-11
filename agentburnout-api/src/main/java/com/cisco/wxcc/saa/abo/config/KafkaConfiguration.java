package com.cisco.wxcc.saa.abo.config;

import com.cisco.wxcc.saa.abo.constants.AppConstants;
import com.cisco.wxcc.saa.abo.exceptions.ConfigurationException;
import com.cisco.wxcc.saa.abo.utils.CatalogUtils;
import com.jasongoodwin.monads.Try;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.cisco.wxcc.saa.abo.constants.AppConstants.CATALOGUE_URL;

@Configuration
public class KafkaConfiguration {

    @Bean
    public ProducerFactory<String, Object> producerFactory() throws ConfigurationException {

        Map<String, Object> config = new HashMap<>();

        Try<String> catalogueResponse = CatalogUtils.getCatalogConfiguration(CATALOGUE_URL);
        if (catalogueResponse.isSuccess())
        {
            String catalogueJson = catalogueResponse.getUnchecked();
            config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, CatalogUtils.getValFromJson(catalogueJson, AppConstants.KAFKA_URL_JSON_PATH));
        }
        else
            throw new ConfigurationException("Unable to get catalogue response from: " + CATALOGUE_URL);

//        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() throws ConfigurationException {
        return new KafkaTemplate<String, Object>(producerFactory());
    }
}