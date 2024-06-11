package com.cisco.wxcc.saa.abo.entity;

import com.cisco.wxcc.saa.abo.convertor.ZonedDateTimeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Table(name = "model")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentBurnoutPreTrainedModel {
    @Id
    private String agentId;
    private String orgId;
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime lastTrainedDateTime;
    private byte[] model;
    private int status;

}
