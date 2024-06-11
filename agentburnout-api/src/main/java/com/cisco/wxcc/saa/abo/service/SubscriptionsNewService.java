package com.cisco.wxcc.saa.abo.service;

import com.cisco.wxcc.saa.abo.entity.SubscriptionsNew;
import com.cisco.wxcc.saa.abo.exceptions.IntegrationException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface SubscriptionsNewService {
    ResponseEntity<?> saveSubscription(SubscriptionsNew subscriptions);


    JSONObject getSubscriptionId(String clientType, String accessToken) throws JSONException, IOException, InterruptedException, IntegrationException;



    JSONObject subscribe(String orgId,String ownerId,String role ,String clientType,String accessToken) throws IntegrationException, JSONException, IOException, InterruptedException;
}
