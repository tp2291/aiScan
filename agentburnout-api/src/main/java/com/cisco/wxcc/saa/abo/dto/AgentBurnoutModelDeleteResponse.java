package com.cisco.wxcc.saa.abo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class AgentBurnoutModelDeleteResponse {
    private String agentId;
    private String orgId;
    private ZonedDateTime lastTrainedDateTime;
}
