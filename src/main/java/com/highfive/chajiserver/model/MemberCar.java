package com.highfive.chajiserver.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberCar {
    private int idx;
    private int memberIdx;
    private int carDataIdx;
    private String nickname;
    private boolean isMain;
}
