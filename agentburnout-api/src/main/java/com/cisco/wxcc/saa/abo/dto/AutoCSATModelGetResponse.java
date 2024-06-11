package com.cisco.wxcc.saa.abo.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class AutoCSATModelGetResponse {

    private String orgId;
    private Timestamp lastTrainedTimeStamp;
    private Float r2Score;
    private Float accuracy;
    private Float dataSetSize;
    private int status;
}
