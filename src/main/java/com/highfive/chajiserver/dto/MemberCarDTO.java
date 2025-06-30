package com.highfive.chajiserver.dto;

import lombok.Data;

@Data
public class MemberCarDTO {
    private int idx;
    private int memberIdx;
    private int carDataIdx;
    private String nickname;
    private boolean isMain;

    // JOIN으로 가져올 car_data 필드
    private String brand;
    private String model;
    private int year;
    private String trim;
}
