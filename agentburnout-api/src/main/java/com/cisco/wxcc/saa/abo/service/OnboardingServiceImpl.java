package com.cisco.wxcc.saa.abo.service;

import com.cisco.wxcc.saa.abo.entity.AgentBurnoutOnboarding;
import com.cisco.wxcc.saa.abo.entity.AutoCSATOnboarding;
import com.cisco.wxcc.saa.abo.repository.AgentBurnoutOnboardingRepository;
import com.cisco.wxcc.saa.abo.repository.AutoCSATOnboardingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OnboardingServiceImpl implements OnboardingService {

    private final AgentBurnoutOnboardingRepository agentBurnoutOnboardingRepository;

    private final AutoCSATOnboardingRepository autoCSATOnboardingRepository;

    @Autowired
    public OnboardingServiceImpl(AgentBurnoutOnboardingRepository agentBurnoutOnboardingRepository, AutoCSATOnboardingRepository autoCSATOnboardingRepository) {
        this.agentBurnoutOnboardingRepository = agentBurnoutOnboardingRepository;
        this.autoCSATOnboardingRepository = autoCSATOnboardingRepository;
    }

    @Override
    public ResponseEntity<?> saveAgentBurnoutOnboardingDetails(AgentBurnoutOnboarding agentBurnoutOnboarding) {
        log.info("{}", agentBurnoutOnboarding);
        return ResponseEntity.ok(agentBurnoutOnboardingRepository.save(agentBurnoutOnboarding));
    }

    @Override
    public ResponseEntity<?> fetchAgentBurnoutOnboardingDetails(String orgId) {
        var onboardingDetailsForOrg = agentBurnoutOnboardingRepository.findByOrgId(orgId);
        return ResponseEntity.ok(onboardingDetailsForOrg);
    }

    @Override
    public ResponseEntity<?> saveAutoCSATOnboardingDetails(AutoCSATOnboarding autoCsatOnboarding) {
        log.info("{}", autoCsatOnboarding);
        return ResponseEntity.ok(autoCSATOnboardingRepository.save(autoCsatOnboarding));
    }

    @Override
    public ResponseEntity<?> fetchAutoCSATOnboardingDetails(String orgId) {
        var onboardingDetailsForOrg = autoCSATOnboardingRepository.findByOrgId(orgId);
        return ResponseEntity.ok(onboardingDetailsForOrg);
    }

}
