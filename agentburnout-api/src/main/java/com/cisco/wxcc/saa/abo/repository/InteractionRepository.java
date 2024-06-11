package com.cisco.wxcc.saa.abo.repository;

import com.cisco.wxcc.saa.abo.entity.Interaction;
import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, String> {
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Interaction i SET i.actionTaken = true, i.actionType = :action_type, i.actionDateTime = :current_zoned_timestamp where i.interactionId = :interaction_id")
    void giveBreak(@Param("action_type") String actionType, @Param("interaction_id") String interactionId, @Param("current_zoned_timestamp") ZonedDateTime currentTimestamp);

    Page<Interaction> findByAgentIdOrderByInteractionDateTimeDesc(String agentId, Pageable pageable);

    @Query("SELECT m FROM Interaction m WHERE (m.agentId) IN :agents AND m.interactionDateTime = (SELECT MAX(m2.interactionDateTime) FROM Interaction m2 WHERE m2.agentId = m.agentId AND m2.actionTaken = true) ORDER BY m.agentId")
    List<Interaction> getLatestBurnoutMetricForAgentList(@Param("agents") List<String> agents, Pageable pageable);

    @Query("SELECT m FROM Interaction m WHERE (m.agentId) IN :agents AND m.interactionDateTime = (SELECT MAX(m2.interactionDateTime) FROM Interaction m2 WHERE m2.agentId = m.agentId) ORDER BY m.agentId")
    List<Interaction> getLatestMetricForAgentList(@Param("agents") List<String> agents, Pageable pageable);



    @Query(value = "SELECT i FROM Interaction i WHERE i.agentId = :agent_id ORDER BY i.interactionDateTime DESC")
    List<Interaction> getLatestInteraction(@Param("agent_id") String agentId, Pageable pageable);

    @Query("SELECT i FROM Interaction i WHERE i.agentId = :agent_id AND i.agentSessionId = :agent_session_id AND i.actionTaken = true ORDER BY i.interactionDateTime DESC")
    List<Interaction> getBurnoutInteractionsByAgentSessionId(@Param("agent_id") String agentId, @Param("agent_session_id") String agentSessionId, Pageable pageable);

    @PostConstruct
    default void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
