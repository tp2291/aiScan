package com.cisco.wxcc.saa.abo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "config")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentBurnoutOnboarding {

    @Id
    @Column(name = "org_id")
    private String orgId;

    @Column(name = "team_ids")
    @ElementCollection(targetClass=String.class)
    private List<String> teamIds;

    @Column(name = "agent_ids")
    @ElementCollection(targetClass=String.class)
    private List<String> agentIds;

    @Column(name = "idle_codes")
    @ElementCollection(targetClass=String.class)
    private List<String> idleCodes;
}
