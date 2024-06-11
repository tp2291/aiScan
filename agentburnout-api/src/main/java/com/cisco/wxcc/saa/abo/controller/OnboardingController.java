package com.cisco.wxcc.saa.abo.controller;

import com.cisco.wxcc.saa.abo.config.Auth;
import com.cisco.wxcc.saa.abo.dto.AgentBurnoutOnboardingRequest;
import com.cisco.wxcc.saa.abo.dto.AutoCSATOnboardingRequest;
import com.cisco.wxcc.saa.abo.entity.AgentBurnoutOnboarding;
import com.cisco.wxcc.saa.abo.entity.AutoCSATOnboarding;
import com.cisco.wxcc.saa.abo.service.OnboardingService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
public class OnboardingController {
    private final OnboardingService onboardingService;

    private final ModelMapper modelMapper;

    public OnboardingController(OnboardingService onboardingService, ModelMapper modelMapper) {
        this.onboardingService = onboardingService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/agentburnout/onboarding")
    public ResponseEntity<?> saveAgentBurnoutOnboardingDetails(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody AgentBurnoutOnboardingRequest agentBurnoutOnboardingRequest) {
        log.info("Received POST request to save AgentBurnoutOnboardingDetails for org id: {} ", agentBurnoutOnboardingRequest);
        if (Auth.getInstance().isInValidToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
        }
        var agentBurnoutOnboarding = modelMapper.map(agentBurnoutOnboardingRequest, AgentBurnoutOnboarding.class);
        return onboardingService.saveAgentBurnoutOnboardingDetails(agentBurnoutOnboarding);
    }

    @GetMapping("/agentburnout/onboarding")
    public ResponseEntity<?> fetchAgentBurnoutOnboardingDetails(@RequestHeader("Authorization") String authorizationHeader, @RequestParam String orgId) {

        log.info("Received GET request to fetch AgentBurnoutOnboardingDetails for org id: {}", orgId);
        if (Auth.getInstance().isInValidToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
        }
        return onboardingService.fetchAgentBurnoutOnboardingDetails(orgId);
    }

    @PostMapping("/autocsat/onboarding")
    public ResponseEntity<?> saveAutoCSATOnboardingDetails(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody AutoCSATOnboardingRequest autoCSATOnboardingRequest) {
        log.info("Received POST request to save AutoCSATOnboardingDetails for org id: {} ", autoCSATOnboardingRequest);
        if (Auth.getInstance().isInValidToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
        }
        var autoCsatOnboarding = modelMapper.map(autoCSATOnboardingRequest, AutoCSATOnboarding.class);
        return onboardingService.saveAutoCSATOnboardingDetails(autoCsatOnboarding);
    }

    @GetMapping("/autocsat/onboarding")
    public ResponseEntity<?> fetchAutoCSATOnboardingDetails(@RequestHeader("Authorization") String authorizationHeader, @RequestParam String orgId) {

        log.info("Received GET request to fetch AutoCSATOnboardingDetails for org id: {}", orgId);
        if (Auth.getInstance().isInValidToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
        }
        return onboardingService.fetchAutoCSATOnboardingDetails(orgId);
    }
}
