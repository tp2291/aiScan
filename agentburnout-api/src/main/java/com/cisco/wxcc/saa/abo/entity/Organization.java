/*package com.cisco.wxcc.saa.abo.entity;


import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "org_info")
public class Organization {

    @Id
    @GeneratedValue
    private long id;
    private String organizationId;

    @OneToMany(mappedBy="organization", cascade = CascadeType.ALL)
    private List<SubscriptionsNew> subscriptions = new ArrayList<>();;

    @OneToOne(mappedBy = "organization")
    private Owner owner;

    public String getOrgId() {
        return organizationId;
    }

    public void setOrgId(String orgId) {
        this.organizationId = orgId;
    }

    public List<SubscriptionsNew> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<SubscriptionsNew> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }
}
*/