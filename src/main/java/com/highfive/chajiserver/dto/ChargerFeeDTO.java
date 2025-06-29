package com.highfive.chajiserver.dto;

import lombok.Data;

@Data
public class ChargerFeeDTO {
    private String busiId;
    private String bnm;
    private Double fastMemberPrice;
    private Double fastNonmemberPrice;
    private Double lowMemberPrice;
    private Double lowNonmemberPrice;
}

