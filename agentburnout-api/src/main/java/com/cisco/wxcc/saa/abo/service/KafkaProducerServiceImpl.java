package com.cisco.wxcc.saa.abo.service;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.cisco.wxcc.saa.abo.constants.AppConstants.MOCK_EVENT_TOPIC_NAME;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private static final String TOPIC = MOCK_EVENT_TOPIC_NAME;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String  message) {
        this.kafkaTemplate.send(TOPIC, "", message);
    }
}
