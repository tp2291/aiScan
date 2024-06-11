package com.cisco.wxcc.saa.abo.service;

import com.cisco.wxcc.saa.abo.repository.AutoCSATModelValidationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ModelValidationServiceImpl implements ModelValidationService {

    private final AutoCSATModelValidationRepository autoCSATModelValidationRepository;

    @Autowired
    public ModelValidationServiceImpl(AutoCSATModelValidationRepository autoCSATModelValidationRepository) {
        this.autoCSATModelValidationRepository = autoCSATModelValidationRepository;
    }


    @Override
    public ResponseEntity<?> fetchAutoCSATModelValidation(String orgId) {
        var autoCSATModelValidation = autoCSATModelValidationRepository.findAllByOrgIdOrderByValidationDateTimeDesc(orgId);
        return ResponseEntity.ok(autoCSATModelValidation);
    }
}
