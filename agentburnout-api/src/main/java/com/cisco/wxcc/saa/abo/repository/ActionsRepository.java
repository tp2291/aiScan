package com.cisco.wxcc.saa.abo.repository;

import com.cisco.wxcc.saa.abo.entity.Actions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionsRepository extends JpaRepository<Actions, String> {

    List<Actions> findByOrgId(String orgId);
}
