package com.cisco.wxcc.saa.abo.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class SubscriptionRequestBody {


    @NotNull
    private String clientType;
    public String getClientType() {
        return clientType;
    }



}
