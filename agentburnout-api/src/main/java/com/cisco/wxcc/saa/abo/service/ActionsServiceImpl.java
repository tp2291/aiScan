package com.cisco.wxcc.saa.abo.service;

import com.cisco.wxcc.saa.abo.dto.ActionsRequest;
import com.cisco.wxcc.saa.abo.entity.Actions;
import com.cisco.wxcc.saa.abo.repository.ActionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ActionsServiceImpl implements ActionsService{

    private final ActionsRepository actionsRepository;

    @Autowired
    public ActionsServiceImpl(ActionsRepository actionsRepository) {
        this.actionsRepository = actionsRepository;
    }

    @Override
    public ResponseEntity<Actions> saveActions(ActionsRequest actionsRequest) {
        log.info("{}", actionsRequest);
        Actions actions = new Actions();
        actions.setInteractionId(actionsRequest.getInteractionId());
        actions.setOrgId(actionsRequest.getOrgId());
        actions.setAgentId(actionsRequest.getAgentId());
        actions.setClientId(actionsRequest.getClientId());
        actions.setActionType(actionsRequest.getActionType());
        actions.setActionDateTime(Long.parseLong(actionsRequest.getActionDateTime()));
        actions.setCreatedDateTime(Instant.now().toEpochMilli());
        return ResponseEntity.ok(actionsRepository.save(actions));
    }
    @Override
    public ResponseEntity<List<Actions>> fetchActions(String orgId) {
        var repositoryByOrgId = actionsRepository.findByOrgId(orgId);
        List<Actions> orgActions = repositoryByOrgId.stream().map(actions -> new Actions(actions.getId(), actions.getInteractionId(), actions.getOrgId(), actions.getAgentId(), actions.getClientId(), actions.getActionType(), actions.getActionDateTime(), actions.getCreatedDateTime())).collect(Collectors.toList());
        return ResponseEntity.ok(orgActions);
    }

}
