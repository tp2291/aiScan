package com.cisco.wxcc.saa.abo.entity;

import com.cisco.wxcc.saa.abo.dto.ConfusionMatrix;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Entity
@Table(schema = "ac", name = "validation")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AutoCSATModelValidation {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "org_id")
    private String orgId;

    @Column(name = "r2score")
    private Float r2score;

    @Column(name = "accuracy")
    private Float accuracy;

    @Column(name = "confusion_matrix", columnDefinition = "json")
    @Type(JsonType.class)
    private ConfusionMatrix confusionMatrix;

    @Column(name = "training_dataset_size")
    private Float trainingDatasetSize;

    @Column(name = "model_trained_date_time")
    private java.sql.Timestamp modelTrainedDateTime;

    @Column(name = "model_version")
    private String modelVersion;

    @Column(name = "validation_date_time")
    private java.sql.Timestamp validationDateTime;

    @Column(name = "validation_data_size")
    private Float validationDataSize;
}
