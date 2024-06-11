package com.cisco.wxcc.saa.abo.repository;

import com.cisco.wxcc.saa.abo.entity.AutoCSATOnboarding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoCSATOnboardingRepository extends JpaRepository<AutoCSATOnboarding, String> {

    AutoCSATOnboarding findByOrgId(String orgId);
}
