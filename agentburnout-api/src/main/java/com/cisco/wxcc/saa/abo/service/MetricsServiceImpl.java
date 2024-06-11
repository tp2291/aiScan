package com.cisco.wxcc.saa.abo.service;

import com.cisco.wxcc.saa.abo.dto.Metrics;
import com.cisco.wxcc.saa.abo.entity.Interaction;
import com.cisco.wxcc.saa.abo.repository.InteractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MetricsServiceImpl implements MetricsService {

    private final InteractionRepository interactionRepository;

    @Autowired
    public MetricsServiceImpl(InteractionRepository interactionRepository) {
        this.interactionRepository = interactionRepository;
    }

    @Override
    public List<Metrics> getAgentMetrics(String agentId, Pageable paging) {
        Page<Interaction> interactions = interactionRepository.findByAgentIdOrderByInteractionDateTimeDesc(agentId, paging);
        return interactions.toList().stream()
                .map(interaction -> new Metrics(interaction.getInteractionId(), interaction.getAgentId(), interaction.getInteractionDateTime(), interaction.getBurnoutIndex(), interaction.getActionDateTime(), interaction.getActionType()))
                .collect(Collectors.toList());
//        return null;
    }

    @Override
    public List<Metrics> getLatestMetricForAgentList(List<String> agents, boolean actionTaken, Pageable paging) {
        List<Interaction> interactions;
        if (actionTaken) {
            interactions = interactionRepository.getLatestBurnoutMetricForAgentList(agents, paging);
        } else {
            interactions = interactionRepository.getLatestMetricForAgentList(agents, paging);
        }
        return interactions.stream()
                .map(interaction -> new Metrics(interaction.getInteractionId(), interaction.getAgentId(), interaction.getInteractionDateTime(), interaction.getBurnoutIndex(), interaction.getActionDateTime(), interaction.getActionType()))
                .collect(Collectors.toList());
    }

}
