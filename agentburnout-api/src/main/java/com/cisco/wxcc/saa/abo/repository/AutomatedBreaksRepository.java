package com.cisco.wxcc.saa.abo.repository;

import com.cisco.wxcc.saa.abo.entity.AutomatedBreaks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutomatedBreaksRepository extends JpaRepository<AutomatedBreaks, String> {

    List<AutomatedBreaks> findByOrgId(String orgId);
}
