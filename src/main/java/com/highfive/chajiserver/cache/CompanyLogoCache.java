package com.highfive.chajiserver.cache;


import java.util.HashMap;
import java.util.Map;

public class CompanyLogoCache {
    private static final Map<String, String> logoMap = new HashMap<>();
    static {
        // 하드코딩된 코드-로고 URL 매핑
        logoMap.put("KP", "/img/logos/kepco.png");       // 한국전력
        logoMap.put("EV", "/img/logos/everon.png");      // 에버온
        logoMap.put("TE", "/img/logos/tesla.png");       // 테슬라
        logoMap.put("ME", "/img/logos/moe.png");         // 환경부
        logoMap.put("PI", "/img/logos/gscharge.png");    // GS차지비
        logoMap.put("HD", "/img/logos/hyundai.png");     // 현대자동차
        logoMap.put("SK", "/img/logos/skenergy.png");    // SK에너지
        // ... ⚠️ 나머지 코드는 필요 시 계속 추가
    }

    // 정적 접근 메서드
    public static String getLogoUrl(String code) {
        return logoMap.getOrDefault(code, "/img/logos/default.png");
    }

}
