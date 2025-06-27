package com.highfive.chajiserver.dto;

import lombok.Data;

import java.util.List;

@Data
public class StationFilterDTO {
    private boolean freeParking;
    private boolean noLimit;
    private int outputMin = 0;
    private int outputMax = 350;
    private List<String> type;       // ex: ["01", "02", ...]
    private List<String> provider;   // ex: ["AM", "AP", ...]

    private String priority; // 사용자의 충전소 선택 우선순위 // "speed" | "reliability" | "comfort"
}
