package com.cisco.wxcc.saa.abo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class BreakEligibilityResponse {
    private boolean breakEligibility;
    private String breakIneligibilityReason;
}
