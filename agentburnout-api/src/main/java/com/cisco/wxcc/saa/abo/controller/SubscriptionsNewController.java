package com.cisco.wxcc.saa.abo.controller;

import com.cisco.wxcc.saa.abo.config.Auth;
import com.cisco.wxcc.saa.abo.dto.SubscriptionRequestBody;
import com.cisco.wxcc.saa.abo.dto.SubscriptionResponseBody;
import com.cisco.wxcc.saa.abo.exceptions.IntegrationException;
import com.cisco.wxcc.saa.abo.service.SubscriptionsNewService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.Objects;

@Slf4j
@RestController
@Validated
public class SubscriptionsNewController {

    private final SubscriptionsNewService subscriptionsService;

    private final ModelMapper modelMapper;





    @Autowired
    public SubscriptionsNewController(SubscriptionsNewService subscriptionsService, ModelMapper modelMapper) {
        this.subscriptionsService = subscriptionsService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/agentburnout/subscribe")
    public ResponseEntity<?> subscribe(@RequestHeader("Authorization") String authorizationHeader,@RequestHeader(value = "X-ORGANIZATION-ID", required = false) String organizationId,  @Valid @NonNull @RequestBody SubscriptionRequestBody subscriptionRequestBody) throws IntegrationException, JSONException, IOException, InterruptedException, ParseException {
        //get the list of agents from teamId
        if (Auth.getInstance().isInValidToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
        }
        //Get orgid,role and ownerid from  auth token. we should check if this is an existing owner with owner id orgid and role,
        //if existing owner we update the subscription
        //if new owner we create a new subscription
        if (Objects.equals(organizationId, "")){
            organizationId = Auth.getInstance().getOrgId(authorizationHeader);
        }
        String ownerId = Auth.getInstance().getOwnerId(authorizationHeader);
        String role = Auth.getInstance().getRole(authorizationHeader);
        
        JSONObject response=subscriptionsService.subscribe(organizationId,ownerId,role,subscriptionRequestBody.getClientType(),authorizationHeader);
        String subscriptionId=response.getString("subscriptionId");
        String websocketUrl=response.getString("webSocketUrl");
        SubscriptionResponseBody subscriptionResponseBody= new SubscriptionResponseBody(subscriptionId,websocketUrl);
        if(subscriptionId==null){
            return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body("Can't subscribe");
        }
        return ResponseEntity.status(HttpStatus.OK).body(subscriptionResponseBody);


    }
}
