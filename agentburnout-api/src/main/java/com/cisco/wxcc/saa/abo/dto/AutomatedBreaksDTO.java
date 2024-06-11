package com.cisco.wxcc.saa.abo.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class AutomatedBreaksDTO {
    @NotNull
    private String agentId;
    @NotNull
    private String orgId;
    @NotNull
    private Boolean automatedBreaksStatus;
}
