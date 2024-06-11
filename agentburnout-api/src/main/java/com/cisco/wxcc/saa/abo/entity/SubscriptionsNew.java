package com.cisco.wxcc.saa.abo.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "subscriptionsnew_info")
public class SubscriptionsNew {
    @Id
    @GeneratedValue
    private Long id;



    private String subscriptionUrlId;


    private String organizationId;

    @OneToOne(mappedBy = "subscription", cascade = CascadeType.ALL)
    private Owner owner;

    public String getSubscriptionId() {
        return subscriptionUrlId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionUrlId = subscriptionId;
    }



    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }
    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
}
