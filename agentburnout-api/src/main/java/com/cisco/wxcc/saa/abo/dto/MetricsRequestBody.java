package com.cisco.wxcc.saa.abo.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class MetricsRequestBody {
    List<String> agentIds;
    @NotNull
    boolean actionTaken;
}
