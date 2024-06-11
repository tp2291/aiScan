package com.cisco.wxcc.saa.abo.entity;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Data
@Table(name = "automated_breaks")
public class AutomatedBreaks {
    @Id
    private String agentId;
    private String orgId;
    private Boolean automatedBreaksStatus;
}
