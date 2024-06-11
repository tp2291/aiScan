package com.cisco.wxcc.saa.abo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(schema = "ac", name = "model")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AutoCSATPreTrainedModel {
    @Id
    @Column(name="orgid")
    private String orgId;

    @Column(name="r2score")
    private Float r2Score;

    @Column(name="accuracy")
    private Float accuracy;

    @Column(name = "datasetsize")
    private Float dataSetSize;

    @Column(name = "lasttrained")
    private Timestamp lastTrained;

    @Column(name="model")
    private byte[] model;

    @Column(name="status")
    private int status;

}