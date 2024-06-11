package com.cisco.wxcc.saa.abo.controller;

import com.cisco.wxcc.saa.abo.config.Auth;
import com.cisco.wxcc.saa.abo.dto.AutomatedBreaksDTO;
import com.cisco.wxcc.saa.abo.entity.AutomatedBreaks;
import com.cisco.wxcc.saa.abo.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;


@Slf4j
@RestController
@Validated
@RequestMapping("/agentburnout")
public class ConfigController {

    private final
    ConfigService configService;

    private final ModelMapper modelMapper;

    @Autowired
    public ConfigController(ConfigService configService, ModelMapper modelMapper) {
        this.configService = configService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/configs")
    public ResponseEntity<?> fetchConfig(@RequestHeader("Authorization") String authorizationHeader, @RequestParam String orgId) {

        log.info("received GET request to fetch automated breaks status for org id: {}", orgId);
        if (Auth.getInstance().isInValidToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
        }
        return configService.fetchConfig(orgId);
    }

    @PostMapping("/configs")
    public ResponseEntity<?> saveConfig(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody AutomatedBreaksDTO automatedBreaksDTO) {
        log.info("received POST request to update automated breaks status with the following config: {}", automatedBreaksDTO);
        if (Auth.getInstance().isInValidToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
        }
        var automatedBreaks = modelMapper.map(automatedBreaksDTO, AutomatedBreaks.class);
        return configService.saveConfig(automatedBreaks);
    }
}
