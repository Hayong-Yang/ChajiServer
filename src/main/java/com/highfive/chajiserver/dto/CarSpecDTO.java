package com.highfive.chajiserver.dto;

import lombok.Data;

@Data
public class CarSpecDTO {
    private int carIdx;
    private String carBrand;
    private String carModel;
    private int car_Year;
    private String car_Trim;
    private int batteryCapacity;
    private boolean supportsAcSlow;
    private boolean supportsDcCombo;
    private boolean supportsDcChademo;
    private boolean supportsAcThreePhase;
    private boolean supportsTesla;
    private boolean supportsMobilePlug;
    private boolean supportsWireless;
    private double evEfficiency;
    private double cityEv;
    private double highwayEv;
    private double cityEvRatio;
    private double highwayEvRatio;
}