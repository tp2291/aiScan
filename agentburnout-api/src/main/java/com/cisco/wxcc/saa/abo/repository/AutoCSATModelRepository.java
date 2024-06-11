package com.cisco.wxcc.saa.abo.repository;

import com.cisco.wxcc.saa.abo.entity.AutoCSATPreTrainedModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoCSATModelRepository extends JpaRepository<AutoCSATPreTrainedModel, String> {
}