package com.cisco.wxcc.saa.abo.service;

import com.cisco.wxcc.saa.abo.dto.Metrics;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MetricsService {

    List<Metrics> getAgentMetrics(String agentId, Pageable paging);

    List<Metrics> getLatestMetricForAgentList(List<String> agents, boolean actionTaken, Pageable paging);
}
