package com.highfive.chajiserver.model;

import lombok.Data;

@Data
public class CarData {
    private int idx;
    private String brand;
    private String model;
    private int year;
    private String trim;
    private double batteryCapacity;
    private boolean connectorAcSlow;
    private boolean connectorDcCombo;
    private boolean connectorDcChademo;
    private boolean connectorAcThreePhase;
    private boolean connectorTesla;
    private boolean connectorMobilePlug;
    private boolean connectorWireless;
    private double evEfficiency;
    private double cityEv;
    private double highwayEv;
    private double cityEvRatio;
    private double highwayEvRatio;
}
