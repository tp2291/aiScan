package com.cisco.wxcc.saa.abo.repository;

import com.cisco.wxcc.saa.abo.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {





    Owner findByUserIdAndOrganizationId(String ownerId, String orgId);
}
