package com.highfive.chajiserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteStationDTO {
    private int idx;
    private int memberIdx;
    private String statId;
    private LocalDateTime createdAt;
}
