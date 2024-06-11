package com.cisco.wxcc.saa.abo.service;

import com.cisco.wxcc.saa.abo.dto.MockEventDTO;
import org.springframework.http.ResponseEntity;

public interface MockEventService {
    ResponseEntity<?> mockEvent(MockEventDTO mockEventDTO);
}
