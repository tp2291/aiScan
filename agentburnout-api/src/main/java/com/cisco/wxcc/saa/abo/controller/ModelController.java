package com.cisco.wxcc.saa.abo.controller;

import com.cisco.wxcc.saa.abo.config.Auth;
import com.cisco.wxcc.saa.abo.service.ModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class ModelController {
    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @DeleteMapping("/agentburnout/models")
    public ResponseEntity<?> deleteAgentBurnoutModelByAgentId(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("agentId") String agentId, @RequestParam("orgId") String orgId) {
        log.info("Delete request received for pre-trained agent burnout model of agent id: {} and org id: {}", agentId, orgId);
        if (Auth.getInstance().isInValidToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
        }
        return ResponseEntity.status(HttpStatus.OK).body(modelService.deleteAgentBurnoutModelByAgentId(agentId, orgId));
    }

    @GetMapping("/agentburnout/models")
    public ResponseEntity<?> getAgentBurnoutModelByAgentId(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("agentId") String agentId, @RequestParam("orgId") String orgId) {

        log.info("Get request received for pre-trained agent burnout model of agent id: {} and org id: {}", agentId, orgId);
        if (Auth.getInstance().isInValidToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
        }
        return ResponseEntity.status(HttpStatus.OK).body(modelService.getAgentBurnoutModelByAgentId(agentId, orgId));
    }

    @DeleteMapping("/autocsat/models")
    public ResponseEntity<?> deleteAutoCSATModelByOrgId(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("orgId") String orgId) {
        log.info("Delete request received for pre-trained autoCSAT model of org id: {} ", orgId);
        if (Auth.getInstance().isInValidToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
        }
        return ResponseEntity.status(HttpStatus.OK).body(modelService.deleteAutoCSATtModelByOrgId(orgId));
    }

    @GetMapping("/autocsat/models")
    public ResponseEntity<?> getAutoCSATModelByOrgId(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("orgId") String orgId) {

        log.info("Get request received for pre-trained autoCSAT model of org id: {} ", orgId);
        if (Auth.getInstance().isInValidToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
        }
        return ResponseEntity.status(HttpStatus.OK).body(modelService.getAutoCSATModelByOrgId(orgId));
    }
}
