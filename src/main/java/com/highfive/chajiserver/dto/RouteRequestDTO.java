package com.highfive.chajiserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequestDTO {
    private List<LatLngDTO> waypoints;
    private boolean highway;
    private LatLngDTO origin;
    private LatLngDTO dest;
    private double distance;

    //필터관련
    private boolean freeParking;
    private boolean noLimit;
    private int outputMin = 0;
    private int outputMax = 350;
    private List<String> type;
    private List<String> provider;
    private String priority;
}
