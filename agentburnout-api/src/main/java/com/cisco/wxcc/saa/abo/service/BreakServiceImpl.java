package com.cisco.wxcc.saa.abo.service;

import com.cisco.wxcc.saa.abo.dto.BreakEligibilityResponse;
import com.cisco.wxcc.saa.abo.dto.Metrics;
import com.cisco.wxcc.saa.abo.entity.AutomatedBreaks;
import com.cisco.wxcc.saa.abo.entity.Interaction;
import com.cisco.wxcc.saa.abo.repository.AutomatedBreaksRepository;
import com.cisco.wxcc.saa.abo.repository.InteractionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cisco.wxcc.saa.abo.constants.BusinessConstants.*;

@Slf4j
@Service
public class BreakServiceImpl implements BreakService {

    private final InteractionRepository interactionRepository;

    private final AutomatedBreaksRepository automatedBreaksRepository;

    @Autowired
    public BreakServiceImpl(InteractionRepository interactionRepository, AutomatedBreaksRepository automatedBreaksRepository) {
        this.interactionRepository = interactionRepository;
        this.automatedBreaksRepository = automatedBreaksRepository;
    }

    @Override
    public ResponseEntity<BreakEligibilityResponse> checkBreakEligibility(String agentId) {

        Optional<AutomatedBreaks> automatedBreaks = automatedBreaksRepository.findById(agentId);
        log.info("{}", automatedBreaks);
        if (automatedBreaks.isPresent() && Boolean.FALSE.equals(automatedBreaks.get().getAutomatedBreaksStatus())) {
            return ResponseEntity.ok(new BreakEligibilityResponse(false, AUTOMATED_BREAKS_OFF));
        }

        List<Interaction> latestInteractions = interactionRepository.getLatestInteraction(agentId, PageRequest.of(0, 1));
        log.info("{}", latestInteractions);
        if (latestInteractions.isEmpty() || Duration.between(latestInteractions.get(0).getInteractionDateTime(), ZonedDateTime.now()).toHours() > 8) {
            return ResponseEntity.ok(BreakEligibilityResponse.builder().breakEligibility(true).build());
        }

        String currentAgentSessionId = latestInteractions.get(0).getAgentSessionId();
        log.info("{}", currentAgentSessionId);

        List<Interaction> interactions = interactionRepository.getBurnoutInteractionsByAgentSessionId(agentId, currentAgentSessionId, PageRequest.of(0, 2));

        log.info("{}", interactions);

        List<Metrics> metrics = interactions.stream().map(interaction -> new Metrics(interaction.getInteractionId(), interaction.getAgentId(), interaction.getInteractionDateTime(), interaction.getBurnoutIndex(), interaction.getActionDateTime(), interaction.getActionType())).collect(Collectors.toList());

        log.info("{}", metrics);


        if (metrics.isEmpty())
            return ResponseEntity.ok(BreakEligibilityResponse.builder().breakEligibility(true).build());

        log.info("{}", metrics.get(0).getActionDateTime());

        if (Duration.between(metrics.get(0).getActionDateTime(), ZonedDateTime.now()).toHours() < MINIMUM_GAP_BETWEEN_TWO_CONSECUTIVE_BREAKS_IN_HOURS) {
            return ResponseEntity.ok(new BreakEligibilityResponse(false, MINIMUM_GAP_BETWEEN_TWO_CONSECUTIVE_BREAKS_NOT_ELAPSED));
        }

        if (metrics.size() >= MAX_BREAKS_PER_SHIFT) {
            return ResponseEntity.ok(new BreakEligibilityResponse(false, MAX_BREAKS_PER_SHIFT_EXHAUSTED));
        }

        return ResponseEntity.ok(BreakEligibilityResponse.builder().breakEligibility(true).build());

    }

    @Override
    public ResponseEntity<Interaction> giveBreak(String interactionId) {

//        Optional<Interaction> interaction = interactionRepository.findById(interactionId);

//        if (interaction.isEmpty()) {
//            throw new InteractionNotFoundException();
//        }
//
//        if (!checkBreakEligibility(interaction.get().getAgentId()).getBody().breakEligibility()) {
//            throw new InvalidBreakException();
//        }

        interactionRepository.giveBreak(VIDEO_PLAYED, interactionId, ZonedDateTime.now());

        return ResponseEntity.ok(interactionRepository.findById(interactionId).get());


    }


}
