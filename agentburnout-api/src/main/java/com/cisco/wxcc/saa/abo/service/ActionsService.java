package com.cisco.wxcc.saa.abo.service;

import com.cisco.wxcc.saa.abo.dto.ActionsRequest;
import com.cisco.wxcc.saa.abo.entity.Actions;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ActionsService {
    ResponseEntity<Actions> saveActions(ActionsRequest actionsRequest);

    ResponseEntity<List<Actions>> fetchActions(String orgId);
}
