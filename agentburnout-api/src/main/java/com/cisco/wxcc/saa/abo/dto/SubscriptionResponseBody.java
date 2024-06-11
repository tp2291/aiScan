package com.cisco.wxcc.saa.abo.dto;

import jakarta.validation.constraints.NotNull;

public class SubscriptionResponseBody {

    @NotNull
    private String subscriptionId;

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getWebsocketUrl() {
        return websocketUrl;
    }

    public void setWebsocketUrl(String websocketUrl) {
        this.websocketUrl = websocketUrl;
    }

    @NotNull
    private String websocketUrl;


    public SubscriptionResponseBody(String subscriptionId, String websocketUrl) {
        this.subscriptionId = subscriptionId;
        this.websocketUrl=websocketUrl;
    }
}