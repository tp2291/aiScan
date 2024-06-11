package com.cisco.wxcc.saa.abo.entity;

import com.cisco.wxcc.saa.abo.convertor.ZonedDateTimeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "burnout")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Interaction {
    @Id
    private String interactionId;
    @Column(name = "interaction_date_time")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime interactionDateTime;
    private String agentId;
    private String orgId;
    private String agentSessionId;
    private Float burnoutIndex;
    private boolean actionTaken;
    @Column(name = "action_date_time")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime actionDateTime;
    private String actionType;
}
