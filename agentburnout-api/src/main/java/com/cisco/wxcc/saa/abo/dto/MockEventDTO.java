package com.cisco.wxcc.saa.abo.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class MockEventDTO {
    @NotNull
    String agentId;
    @NotNull
    String orgId;
    @NotNull
    Float burnoutIndex;
}
