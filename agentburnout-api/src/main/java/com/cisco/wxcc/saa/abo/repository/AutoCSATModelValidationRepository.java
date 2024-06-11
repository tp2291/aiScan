package com.cisco.wxcc.saa.abo.repository;

import com.cisco.wxcc.saa.abo.entity.AutoCSATModelValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoCSATModelValidationRepository extends JpaRepository<AutoCSATModelValidation, String> {

    List<AutoCSATModelValidation> findAllByOrgIdOrderByValidationDateTimeDesc(String orgId);
}