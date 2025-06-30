package com.highfive.chajiserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZoomDTO {
    private String sidoName;
    private String sigunguName;
    private int count;

    private Double lat;
    private Double lon;

    // 생성자 오버로딩 (전국용)
    public ZoomDTO(String sidoName, String sigunguName, int count) {
        this.sidoName = sidoName;
        this.sigunguName = sigunguName;
        this.count = count;
    }

    // ✅ JSON 응답에 name 필드를 자동 포함
    public String getName() {
        if (sigunguName != null && !sigunguName.isEmpty()) {
            return sigunguName;
        } else if (sidoName != null) {
            return sidoName;
        } else {
            return "알 수 없음";
        }
    }
}
