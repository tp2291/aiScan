package com.cisco.wxcc.saa.abo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgentConfig {
    private String agentId;
    private boolean automatedBreaksStatus;
}
