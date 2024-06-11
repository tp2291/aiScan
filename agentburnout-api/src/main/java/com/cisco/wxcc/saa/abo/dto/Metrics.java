package com.cisco.wxcc.saa.abo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Data
public class Metrics {
    private String interactionId;
    private String agentId;
    private ZonedDateTime interactionDateTime;
    private Float burnoutIndex;
    private ZonedDateTime actionDateTime;
    private String actionType;

}
