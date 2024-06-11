package com.cisco.wxcc.saa.abo.repository;

import com.cisco.wxcc.saa.abo.entity.AgentBurnoutOnboarding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentBurnoutOnboardingRepository extends JpaRepository<AgentBurnoutOnboarding, String> {

    AgentBurnoutOnboarding findByOrgId(String orgId);

}
