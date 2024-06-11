package com.cisco.wxcc.saa.abo.service;

import org.springframework.http.ResponseEntity;

public interface ModelValidationService {
    ResponseEntity<?> fetchAutoCSATModelValidation(String orgId);
}
