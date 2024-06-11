package com.cisco.wxcc.saa.abo.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class AgentBurnoutOnboardingRequest {

    @NotNull
    private String orgId;

    private List<String> teamIds;
    private List<String> agentIds;

    @NotNull
    private List<String> idleCodes;

}
