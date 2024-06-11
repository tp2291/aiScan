package com.cisco.wxcc.saa.abo.service;


import com.cisco.wxcc.saa.abo.constants.AppConstants;
import com.cisco.wxcc.saa.abo.entity.Owner;
import com.cisco.wxcc.saa.abo.entity.SubscriptionsNew;
import com.cisco.wxcc.saa.abo.exceptions.IntegrationException;
import com.cisco.wxcc.saa.abo.repository.OwnerRepository;
import com.cisco.wxcc.saa.abo.repository.SubscriptionsNewRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@Slf4j
public class SubscriptionsNewServiceImpl implements SubscriptionsNewService{
    private final SubscriptionsNewRepository subscriptionsRepository;

    private final OwnerRepository ownerRepository;



    private final AppConstants appConstants;

    @Autowired
    public SubscriptionsNewServiceImpl(SubscriptionsNewRepository subscriptionsRepository, OwnerRepository ownerRepository, AppConstants appConstants) {
        this.subscriptionsRepository = subscriptionsRepository;
        this.ownerRepository = ownerRepository;
        this.appConstants = appConstants;
    }




    @Override
    public ResponseEntity<?> saveSubscription(SubscriptionsNew subscriptions) {
        log.info("{}", subscriptions);
        return ResponseEntity.ok(subscriptionsRepository.save(subscriptions));
    }

    @Override
    public JSONObject getSubscriptionId(String clientType, String accessToken) throws JSONException, IOException, InterruptedException, IntegrationException {
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("force", true);
        jsonPayload.put("clientType",clientType);
        jsonPayload.put("allowMultiLogin",true);
        String url =appConstants.NOTIFS_REGISTER_URL;

        String requestBody = jsonPayload.toString();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization",  accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpClient httpClient= HttpClient.newBuilder().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


        if (response.statusCode() == 200) {
            JSONObject responseJson = new JSONObject(response.body());
            return responseJson;
        }

        throw new IntegrationException("Unable to get Authorization code");

    }



    @Override
    public JSONObject subscribe(String organizationId,String ownerId, String role,String clientType, String accessToken) throws IntegrationException, JSONException, IOException, InterruptedException {
        Owner owner =findExistingOwnerById(ownerId,role,organizationId);
        SubscriptionsNew newSubscription =  new SubscriptionsNew();
        JSONObject reponseJson=getSubscriptionId(clientType,accessToken);
        String subscriptionId=reponseJson.getString("subscriptionId");
        log.info("New subscriptionid " + subscriptionId);


        if (owner == null) {
            if (subscriptionId!= null){
                log.info("New subscription/owner");
                Owner owner1= new Owner();
                owner1.setRole(role);
                owner1.setOwnerId(ownerId);
                owner1.setSubscription(newSubscription);
                owner1.setOrganizationId(organizationId);
                newSubscription.setOwner(owner1);
                newSubscription.setOrganizationId(organizationId);
                newSubscription.setSubscriptionId(subscriptionId);
                subscriptionsRepository.save(newSubscription);
                log.info("Subscription saved");



            }
            else {
                log.info("New subscriptionid not found");
                return null;

            }

        }
        else{
            log.info("finding exisiting subscription for orgid " + organizationId);
            Optional<SubscriptionsNew> subscriptionOptional= subscriptionsRepository.findByOrganizationIdAndOwner_UserId(organizationId,ownerId);
            if (subscriptionOptional.isPresent()) {
                SubscriptionsNew existingSubscriptions = subscriptionOptional.get();
                existingSubscriptions.setSubscriptionId(subscriptionId);
                subscriptionsRepository.save(existingSubscriptions);
                log.info("Subscription updated");
                // handle subscription
            } else {
                log.info("Can't find exisiting subscription");
                // handle missing subscription
            }




        }
        log.info("done with subscription");
        return reponseJson;
    }

    private Owner findExistingOwnerById(String ownerId, String role, String orgId) {
        Owner ownerOptional = ownerRepository.findByUserIdAndOrganizationId(ownerId, orgId);
        if (ownerOptional!=null){
            log.info("Existing owner");
            return ownerOptional;


        }

        return null;
    }
}
