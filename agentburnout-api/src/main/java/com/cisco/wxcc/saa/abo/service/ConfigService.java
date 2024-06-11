package com.cisco.wxcc.saa.abo.service;

import com.cisco.wxcc.saa.abo.dto.AgentConfig;
import com.cisco.wxcc.saa.abo.entity.AutomatedBreaks;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ConfigService {
    ResponseEntity<AutomatedBreaks> saveConfig(AutomatedBreaks automatedBreaks);

    ResponseEntity<List<AgentConfig>> fetchConfig(String orgId);
}
