package com.cisco.wxcc.saa.abo.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class ActionsRequest {

    @NotNull
    private String interactionId;
    @NotNull
    private String orgId;
    @NotNull
    private String agentId;
    @NotNull
    private String clientId;
    @NotNull
    private String actionType;
    @NotNull
    private String actionDateTime;

}
