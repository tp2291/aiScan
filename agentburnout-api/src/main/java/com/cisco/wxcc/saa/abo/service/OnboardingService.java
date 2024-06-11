package com.cisco.wxcc.saa.abo.service;

import com.cisco.wxcc.saa.abo.entity.AgentBurnoutOnboarding;
import com.cisco.wxcc.saa.abo.entity.AutoCSATOnboarding;
import org.springframework.http.ResponseEntity;

public interface OnboardingService {

    ResponseEntity<?> saveAgentBurnoutOnboardingDetails(AgentBurnoutOnboarding agentBurnoutOnboarding);

    ResponseEntity<?> fetchAgentBurnoutOnboardingDetails(String orgId);

    ResponseEntity<?> saveAutoCSATOnboardingDetails(AutoCSATOnboarding autoCsatOnboarding);

    ResponseEntity<?> fetchAutoCSATOnboardingDetails(String orgId);
}
