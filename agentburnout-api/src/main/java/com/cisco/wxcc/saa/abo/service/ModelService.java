package com.cisco.wxcc.saa.abo.service;

import com.cisco.wxcc.saa.abo.dto.AgentBurnoutModelDeleteResponse;
import com.cisco.wxcc.saa.abo.dto.AgentBurnoutModelGetResponse;
import com.cisco.wxcc.saa.abo.dto.AutoCSATModelDeleteResponse;
import com.cisco.wxcc.saa.abo.dto.AutoCSATModelGetResponse;

public interface ModelService {
    AgentBurnoutModelDeleteResponse deleteAgentBurnoutModelByAgentId(String agentId, String orgId);
    AgentBurnoutModelGetResponse getAgentBurnoutModelByAgentId(String agentId, String orgId);
    AutoCSATModelDeleteResponse deleteAutoCSATtModelByOrgId(String orgId);
    AutoCSATModelGetResponse getAutoCSATModelByOrgId(String orgId);
}