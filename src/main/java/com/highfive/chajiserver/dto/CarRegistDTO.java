package com.highfive.chajiserver.dto;

import lombok.Data;

@Data
public class CarRegistDTO {
    private Integer memberIdx;
    private Integer carIdx;
    private String carNickname;

    private Boolean acSlow;
    private Boolean dcCombo;
    private Boolean dcChademo;
    private Boolean acThreePhase;
    private Boolean tesla;
    private Boolean mobilePlug;
    private Boolean wireless;

    private Integer userCarIdx;

}
