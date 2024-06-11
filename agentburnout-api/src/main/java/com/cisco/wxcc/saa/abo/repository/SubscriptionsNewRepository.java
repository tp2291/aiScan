package com.cisco.wxcc.saa.abo.repository;

import com.cisco.wxcc.saa.abo.entity.SubscriptionsNew;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionsNewRepository extends JpaRepository<SubscriptionsNew, Long> {


    Optional<SubscriptionsNew> findByOrganizationIdAndOwner_UserId(String orgId,String ownerId);


}
