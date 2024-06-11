package com.cisco.wxcc.saa.abo.service;

import com.cisco.wxcc.saa.abo.dto.BreakEligibilityResponse;
import com.cisco.wxcc.saa.abo.entity.Interaction;
import org.springframework.http.ResponseEntity;

public interface BreakService {
    ResponseEntity<Interaction> giveBreak(String interactionId);

    ResponseEntity<BreakEligibilityResponse> checkBreakEligibility(String agentId);
}
