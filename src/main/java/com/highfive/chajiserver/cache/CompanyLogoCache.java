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
        logoMap.put("AC", "/img/logos/outocripte.png");  // 아우토크립트
        logoMap.put("AH", "/img/logos/ahha.png");        // 아하
        logoMap.put("AL", "/img/logos/alone.png");       // 아론
        logoMap.put("AM", "/img/logos/amanokorea.png");  // 아마노코리아
        logoMap.put("AP", "/img/logos/applemango.png");  // 애플망고
        logoMap.put("BA", "/img/logos/buangun.png");     // 부안군
        logoMap.put("BE", "/img/logos/brightenergy.png");// 브라이트에너지파트너스
        logoMap.put("BG", "/img/logos/begins.png");      // 비긴스
        logoMap.put("BK", "/img/logos/BKenergy.png");    // 비케이에너지
        logoMap.put("BN", "/img/logos/bluenetworks.png");// 블루네트웍스
        logoMap.put("BP", "/img/logos/chabaps.png");     // 차밥스
        logoMap.put("BS", "/img/logos/boss.png");        // 보스시큐리티
        logoMap.put("BT", "/img/logos/botary.png");      // 보타리에너지
        logoMap.put("CA", "/img/logos/cstechnology.png");// 씨에스테크놀로지
        logoMap.put("CB", "/img/logos/chambit.png");     // 참빛이브이씨
        logoMap.put("CC", "/img/logos/cocom.png");       // 코콤
        logoMap.put("CG", "/img/logos/seoulCNG.png");    // 서울씨엔지
        logoMap.put("CH", "/img/logos/chaum.png");       // 채움모빌리티
        logoMap.put("CI", "/img/logos/coolsign.png");    // 쿨사인
        logoMap.put("CN", "/img/logos/cnp.png");         // 에바씨엔피
        logoMap.put("CO", "/img/logos/cadian.png");      // 한전케이디엔
        logoMap.put("CP", "/img/logos/castpro.png");     // 캐스트프로
        logoMap.put("CR", "/img/logos/crocus.png");      // 크로커스
        logoMap.put("CS", "/img/logos/koreaEV.png");     // 한국EV충전서비스센터
        logoMap.put("CT", "/img/logos/citycar.png");     // 씨티카
        logoMap.put("CU", "/img/logos/Cus.png");         // 씨어스
        logoMap.put("CV", "/img/logos/chavi.png");       // 채비
        logoMap.put("DE", "/img/logos/daguegong.png");   // 대구공공시설관리공단
        logoMap.put("DG", "/img/logos/dague.png");       // 대구시
        logoMap.put("DL", "/img/logos/dilive.png");      // 딜라이브
        logoMap.put("DO", "/img/logos/dahansong.png");   // 대한송유관공사
        logoMap.put("DP", "/img/logos/dau.png");         // 대유플러스
        logoMap.put("DR", "/img/logos/duruscob.png");    // 두루스코이브이
        // ... ⚠️ 나머지 코드는 필요 시 계속 추가
    }

    // 정적 접근 메서드
    public static String getLogoUrl(String code) {
        return logoMap.getOrDefault(code, "/img/logos/default.png");
    }

}
