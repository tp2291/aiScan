package com.cisco.wxcc.saa.abo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class AutoCSATModelDeleteResponse {
    private String orgId;
    private Timestamp lastTrainedTimeStamp;
}