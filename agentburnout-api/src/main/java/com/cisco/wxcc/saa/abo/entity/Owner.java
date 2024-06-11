package com.cisco.wxcc.saa.abo.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "owner_info")
public class Owner {

    @Id
    @GeneratedValue
    private long id;

    private String userId;



    private String role;

    @OneToOne
    @JoinColumn(name = "subscriptionId")
    private SubscriptionsNew subscription;

    private String organizationId;

    public String getOwnerId() {
        return userId;
    }

    public void setOwnerId(String ownerId) {
        this.userId = ownerId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public SubscriptionsNew getSubscription() {
        return subscription;
    }

    public void setSubscription(SubscriptionsNew subscription) {
        this.subscription = subscription;
    }


}
