package com.cisco.wxcc.saa.abo.controller;

import com.cisco.wxcc.saa.abo.config.Auth;
import com.cisco.wxcc.saa.abo.dto.ActionsRequest;
import com.cisco.wxcc.saa.abo.service.ActionsService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Slf4j
@RestController
public class ActionsController {
    private final ActionsService actionsService;

    private final ModelMapper modelMapper;

    public ActionsController(ActionsService actionsService, ModelMapper modelMapper) {
        this.actionsService = actionsService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/agentburnout/action")
    public ResponseEntity<?> saveActions(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody ActionsRequest actionsRequest) {
        log.info("Received POST request to save AgentBurnoutOnboardingDetails for org id: {} ", actionsRequest.getOrgId());
        if (Auth.getInstance().isInValidToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
        }
        return actionsService.saveActions(actionsRequest);
    }

    @GetMapping("/agentburnout/action")
    public ResponseEntity<?> fetchActions(@RequestHeader("Authorization") String authorizationHeader, @RequestParam String orgId) {

        log.info("Received GET request to fetch AgentBurnoutOnboardingDetails for org id: {}", orgId);
        if (Auth.getInstance().isInValidToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
        }
        return actionsService.fetchActions(orgId);
    }

}