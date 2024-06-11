package com.cisco.wxcc.saa.abo.service;

import com.cisco.wxcc.saa.abo.dto.AgentConfig;
import com.cisco.wxcc.saa.abo.entity.AutomatedBreaks;
import com.cisco.wxcc.saa.abo.repository.AutomatedBreaksRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {
    private final
    AutomatedBreaksRepository automatedBreaksRepository;

    @Autowired
    public ConfigServiceImpl(AutomatedBreaksRepository automatedBreaksRepository) {
        this.automatedBreaksRepository = automatedBreaksRepository;
    }

    @Override
    public ResponseEntity<List<AgentConfig>> fetchConfig(String orgId) {
        var automatedBreaksForOrg = automatedBreaksRepository.findByOrgId(orgId);
        List<AgentConfig> orgConfig = automatedBreaksForOrg.stream().map(automatedBreaks -> new AgentConfig(automatedBreaks.getAgentId(), automatedBreaks.getAutomatedBreaksStatus())).collect(Collectors.toList());
        return ResponseEntity.ok(orgConfig);
    }

    @Override
    public ResponseEntity<AutomatedBreaks> saveConfig(AutomatedBreaks newAutomatedBreaks) {
        log.info("{}", newAutomatedBreaks);
        return ResponseEntity.ok(automatedBreaksRepository.save(newAutomatedBreaks));
    }


}
