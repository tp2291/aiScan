package com.cisco.wxcc.saa.abo.controller;

import com.cisco.wxcc.saa.abo.config.Auth;
import com.cisco.wxcc.saa.abo.dto.MetricsRequestBody;
import com.cisco.wxcc.saa.abo.service.MetricsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/agentburnout")
public class MetricsController {

    private final MetricsService metricsService;

    @Autowired
    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("{agent_id}/metrics")
    public ResponseEntity<?> getMetrics(@RequestHeader("Authorization") String authorizationHeader,
                                        @PathVariable(value = "agent_id") String agentId,
                                        @RequestParam(value = "page", defaultValue = "0") Integer page,
                                        @RequestParam(value = "size", defaultValue = "5") Integer size) {

        log.info("metrics requested for agentId: {} with page no.: {} and page size: {}", agentId, page, size);
        if (Auth.getInstance().isInValidToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
        }
        Pageable paging = PageRequest.of(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(metricsService.getAgentMetrics(agentId, paging));
    }

    @PostMapping("/metrics")
    public ResponseEntity<?> getMetrics(@RequestHeader("Authorization") String authorizationHeader,
                                        @RequestParam(value = "page", defaultValue = "0") Integer page,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size,
                                        @Valid @RequestBody MetricsRequestBody body) {
        log.info("metrics requested for agentIds: {} with page no.: {} and page size: {}", body, page, size);
        if (Auth.getInstance().isInValidToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
        }
        Pageable paging = PageRequest.of(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(metricsService.getLatestMetricForAgentList(body.getAgentIds(), body.isActionTaken(), paging));
    }
}