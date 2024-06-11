package com.cisco.wxcc.saa.abo.controller;

import com.cisco.wxcc.saa.abo.config.Auth;
import com.cisco.wxcc.saa.abo.service.ModelValidationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@Validated
@RequestMapping("/autocsat")
public class ModelValidationController {

    private final
    ModelValidationServiceImpl modelValidationService;

    @Autowired
    public ModelValidationController(ModelValidationServiceImpl modelValidationService, ModelMapper modelMapper) {
        this.modelValidationService = modelValidationService;
    }

    @GetMapping("/validation")
    public ResponseEntity<?> fetchValidationData(@RequestHeader("Authorization") String authorizationHeader, @RequestParam String orgId) {

        log.info("received GET request to fetch model validation data for org id: {}", orgId);
        if (Auth.getInstance().isInValidToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
        }
        return modelValidationService.fetchAutoCSATModelValidation(orgId);
    }
}
