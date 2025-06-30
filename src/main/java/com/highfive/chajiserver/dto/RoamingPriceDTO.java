package com.highfive.chajiserver.dto;

import lombok.Data;

@Data
public class RoamingPriceDTO {
    private int id;
    private String memberCompany;
    private String operatorCode;
    private String feeInfo;
}
