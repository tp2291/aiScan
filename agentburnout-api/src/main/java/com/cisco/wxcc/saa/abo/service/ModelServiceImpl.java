package com.cisco.wxcc.saa.abo.service;

import com.cisco.wxcc.saa.abo.dto.AgentBurnoutModelDeleteResponse;
import com.cisco.wxcc.saa.abo.dto.AgentBurnoutModelGetResponse;
import com.cisco.wxcc.saa.abo.dto.AutoCSATModelDeleteResponse;
import com.cisco.wxcc.saa.abo.dto.AutoCSATModelGetResponse;
import com.cisco.wxcc.saa.abo.repository.AgentBurnoutModelRepository;
import com.cisco.wxcc.saa.abo.repository.AutoCSATModelRepository;
import org.springframework.stereotype.Service;

@Service
public class ModelServiceImpl implements ModelService {

    private final AgentBurnoutModelRepository agentBurnoutModelRepository;
    private final AutoCSATModelRepository autoCSATModelRepository;

    public ModelServiceImpl(AgentBurnoutModelRepository agentBurnoutModelRepository, AutoCSATModelRepository autoCSATModelRepository) {
        this.agentBurnoutModelRepository = agentBurnoutModelRepository;
        this.autoCSATModelRepository = autoCSATModelRepository;
    }
    @Override
    public AgentBurnoutModelDeleteResponse deleteAgentBurnoutModelByAgentId(String agentId, String orgId) {
        var preTrainedModel = agentBurnoutModelRepository.findByAgentIdAndOrgId(agentId, orgId);
        AgentBurnoutModelDeleteResponse agentBurnoutModelDeleteResponse = new AgentBurnoutModelDeleteResponse(preTrainedModel.getAgentId(), preTrainedModel.getOrgId(), preTrainedModel.getLastTrainedDateTime());
        agentBurnoutModelRepository.deleteByAgentIdAndOrgId(agentId, orgId);
        return agentBurnoutModelDeleteResponse;
    }

    @Override
    public AgentBurnoutModelGetResponse getAgentBurnoutModelByAgentId(String agentId, String orgId) {
        var preTrainedModel = agentBurnoutModelRepository.findByAgentIdAndOrgId(agentId, orgId);
        AgentBurnoutModelGetResponse agentBurnoutModelGetResponse = new AgentBurnoutModelGetResponse(preTrainedModel.getAgentId(), preTrainedModel.getOrgId(), preTrainedModel.getLastTrainedDateTime(), preTrainedModel.getStatus());
        return agentBurnoutModelGetResponse;
    }

    @Override
    public AutoCSATModelDeleteResponse deleteAutoCSATtModelByOrgId(String orgId) {
        var preTrainedModel = autoCSATModelRepository.findById(orgId).get();
        AutoCSATModelDeleteResponse autoCSATModelDeleteResponse = new AutoCSATModelDeleteResponse(preTrainedModel.getOrgId(), preTrainedModel.getLastTrained());
        autoCSATModelRepository.deleteById(orgId);
        return autoCSATModelDeleteResponse;
    }

    @Override
    public AutoCSATModelGetResponse getAutoCSATModelByOrgId(String orgId) {
        var preTrainedModel = autoCSATModelRepository.findById(orgId).get();
        AutoCSATModelGetResponse autoCSATModelGetResponse = new AutoCSATModelGetResponse(preTrainedModel.getOrgId(), preTrainedModel.getLastTrained(), preTrainedModel.getR2Score(), preTrainedModel.getAccuracy(), preTrainedModel.getDataSetSize(), preTrainedModel.getStatus());
        return autoCSATModelGetResponse;
    }
}
