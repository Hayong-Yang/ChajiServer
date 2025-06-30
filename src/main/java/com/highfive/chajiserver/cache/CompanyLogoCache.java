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
        logoMap.put("GR", "/img/logos/skenergy.png");    // 그리드위즈
        logoMap.put("GS", "/img/logos/skenergy.png");    // GS칼텍스
        logoMap.put("HB", "/img/logos/skenergy.png");    // 에이치엘비생명과학
        logoMap.put("HE", "/img/logos/skenergy.png");    // 한국전기차충전서비스
        logoMap.put("HL", "/img/logos/skenergy.png");    // 에이치엘비일렉
        logoMap.put("HM", "/img/logos/skenergy.png");    // 휴맥스이브이
        logoMap.put("HP", "/img/logos/skenergy.png");    // 해피차지
        logoMap.put("HR", "/img/logos/skenergy.png");    // 한국홈충전
        logoMap.put("HS", "/img/logos/skenergy.png");    // 홈앤서비스
        logoMap.put("HW", "/img/logos/skenergy.png");    // 한화솔루션
        logoMap.put("HY", "/img/logos/skenergy.png");    // 현대엔지니어링
        logoMap.put("IM", "/img/logos/skenergy.png");    // 아이마켓코리아
        logoMap.put("IN", "/img/logos/skenergy.png");    // 신세계아이앤씨
        logoMap.put("IO", "/img/logos/skenergy.png");    // 아이온커뮤니케이션즈
        logoMap.put("IV", "/img/logos/skenergy.png");    // 인큐버스
        logoMap.put("JA", "/img/logos/skenergy.png");    // 이브이시스
        logoMap.put("JE", "/img/logos/skenergy.png");    // 제주전기자동차서비스
        logoMap.put("JH", "/img/logos/skenergy.png");    // 종하이앤씨
        logoMap.put("JN", "/img/logos/skenergy.png");    // 제이앤씨플랜
        logoMap.put("KA", "/img/logos/skenergy.png");    // 기아자동차
        logoMap.put("KC", "/img/logos/skenergy.png");    // 한국컴퓨터
        logoMap.put("KE", "/img/logos/skenergy.png");    // 한국전기차인프라기술




    }

    // 정적 접근 메서드
    public static String getLogoUrl(String code) {
        return logoMap.getOrDefault(code, "/img/logos/default.png");
    }

}
