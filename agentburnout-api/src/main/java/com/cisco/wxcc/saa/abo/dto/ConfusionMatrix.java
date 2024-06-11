package com.cisco.wxcc.saa.abo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ConfusionMatrix {

    private static final long serialVersionUID = 1L;

    List<String> columns;
    List<String> index;
    List<List<Integer>> data;
}
