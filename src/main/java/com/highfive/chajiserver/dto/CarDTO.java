package com.highfive.chajiserver.dto;

import lombok.Data;

@Data
public class CarDTO {
    private int carIdx;
    private String carBrand;
    private String carModel;
    private int year;
    private String trim;
    private int batteryCapacity;
    private boolean supportsAcSlow;
    private boolean supportsDcCombo;
    private boolean supportsDcChademo;
    private boolean supportsAcThreePhase;
    private boolean supportsTesla;
    private boolean supportsMobilePlug;
    private boolean supportsWireless;
    private String drivingSys;
    private double efficiency;
    private double cityEv;
    private double highwayEv;
    private double cityEvRatio;
    private double highwayEvRatio;
}