package com.cisco.wxcc.saa.abo.repository;

import com.cisco.wxcc.saa.abo.entity.AgentBurnoutPreTrainedModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AgentBurnoutModelRepository extends JpaRepository<AgentBurnoutPreTrainedModel, String> {
    @Transactional
    public void deleteByAgentIdAndOrgId(String agentId, String orgId);
    public AgentBurnoutPreTrainedModel findByAgentIdAndOrgId(String agentId, String orgId);

}