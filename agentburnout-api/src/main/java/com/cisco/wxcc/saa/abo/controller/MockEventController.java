package com.cisco.wxcc.saa.abo.controller;

import com.cisco.wxcc.saa.abo.config.Auth;
import com.cisco.wxcc.saa.abo.dto.MockEventDTO;
import com.cisco.wxcc.saa.abo.service.MockEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;


@Slf4j
@RestController
@Validated
public class MockEventController {

    private final
    MockEventService mockEventService;

    @Autowired
    public MockEventController(MockEventService mockEventService) {
        this.mockEventService = mockEventService;
    }

    @PostMapping("/agentburnout/mock/event")
    public ResponseEntity<?> mockEvent(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody MockEventDTO mockEventDTO) {
        log.info("received POST request to mock event with the following config: {}", mockEventDTO);
        if (Auth.getInstance().isInValidToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
        }

        return mockEventService.mockEvent(mockEventDTO);
    }
}
