package com.cisco.wxcc.saa.abo.service;

import com.cisco.wxcc.saa.abo.dto.MockEventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class MockEventServiceImpl implements MockEventService {

    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public MockEventServiceImpl(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    public ResponseEntity<?> mockEvent(MockEventDTO mockEventDTO) {
        String agentId = mockEventDTO.getAgentId();
        String orgId = mockEventDTO.getOrgId();
        String burnoutIndex = mockEventDTO.getBurnoutIndex().toString();
        String interactionId = UUID.randomUUID().toString();
        String agentSessionId = UUID.randomUUID().toString();
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        String formattedDateTime = now.format(formatter);
        String message = String.format("{\"interactionId\": \"%s\", \"agentId\": \"%s\", \"orgId\": \"%s\",\"agentSessionId\": \"%s\",\"burnoutIndex\": %s,\"dateTime\": \"%s\",\"eventTime\": \"%s\"}", interactionId, agentId, orgId, agentSessionId, burnoutIndex, formattedDateTime, formattedDateTime);
        kafkaProducerService.sendMessage(message);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
}
