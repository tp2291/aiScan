package com.cisco.wxcc.saa.abo.controller;

import com.cisco.wxcc.saa.abo.config.Auth;
import com.cisco.wxcc.saa.abo.service.BreakService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/agentburnout")
public class BreakController {

    private final BreakService breakService;



    @Autowired
    public BreakController(BreakService breakService) {
        this.breakService = breakService;
    }


    @GetMapping("/breaks")
    public ResponseEntity<?> checkBreakEligibility(@RequestHeader("Authorization") String authorizationHeader, @RequestParam(value = "agentId") String agentId) {
        log.info("break eligibility requested for agentId: {}", agentId);
        if (Auth.getInstance().isInValidToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
        }
        return breakService.checkBreakEligibility(agentId);
    }

    @PostMapping("/breaks")
    public ResponseEntity<?> giveBreak(@RequestHeader("Authorization") String authorizationHeader, @RequestParam(value = "interactionId") String interactionId) {
        log.info("received POST request to give break for interactionId: {}", interactionId);
        if (Auth.getInstance().isInValidToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
        }
        return breakService.giveBreak(interactionId);
    }

}