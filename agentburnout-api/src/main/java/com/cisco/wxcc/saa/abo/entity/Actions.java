package com.cisco.wxcc.saa.abo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "actions")
public class Actions {
    @Id
    @GeneratedValue
    private Long id;
    private String interactionId;
    private String orgId;
    private String agentId;
    private String clientId;
    private String actionType;
    private Long actionDateTime;
    private Long createdDateTime;

}
