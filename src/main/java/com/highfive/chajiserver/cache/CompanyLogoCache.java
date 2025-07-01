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
        logoMap.put("DS", "/img/logos/DS.png");          // 대선==
        logoMap.put("DY", "/img/logos/DY.png");          // 동양이엔피
        logoMap.put("E0", "/img/logos/EO.png");          // 에너지플러스
        logoMap.put("EA", "/img/logos/EA.png");          // 에바
        logoMap.put("EB", "/img/logos/EB.png");          // 일렉트리
        logoMap.put("EC", "/img/logos/EC.png");          // 이지차저
        logoMap.put("EE", "/img/logos/EE.png");          // 이마트
        logoMap.put("EG", "/img/logos/EG.png");          // 에너지파트너즈
        logoMap.put("EH", "/img/logos/EH.png");          // 이앤에이치에너지
        logoMap.put("EK", "/img/logos/EK.png");          // 엔라이튼
        logoMap.put("EM", "/img/logos/EM.png");          // evmost ==
        logoMap.put("EN", "/img/logos/EN.png");          // 이엔
        logoMap.put("EO", "/img/logos/E0.png");          // E1
        logoMap.put("EP", "/img/logos/EP.png");          // 이카플러그
        logoMap.put("ER", "/img/logos/ER.png");          // 이엘일렉트릭
        logoMap.put("ES", "/img/logos/ES.png");          // 이테스
        logoMap.put("ET", "/img/logos/ET.png");          // 이씨티
        logoMap.put("EZ", "/img/logos/EZ.png");          // 차지인
        logoMap.put("FE", "/img/logos/FE.png");          // 에프이씨 ==
        logoMap.put("FT", "/img/logos/FT.png");          // 포티투닷
        logoMap.put("G1", "/img/logos/G1.png");          // 광주시
        logoMap.put("G2", "/img/logos/G2.png");          // 광주시
        logoMap.put("GD", "/img/logos/GD.png");          // 그린도트
        logoMap.put("GE", "/img/logos/GE.png");          // 그린전력
        logoMap.put("GG", "/img/logos/GG.png");          // 강진군
        logoMap.put("GN", "/img/logos/GN.png");          // 지에스커넥트
        logoMap.put("GO", "/img/logos/GO.png");          // 유한회사 골드에너지
        logoMap.put("GP", "/img/logos/GP.png");          // 군포시
        logoMap.put("PM", "/img/logos/PM.png");          // 파라인모터스
        logoMap.put("PS", "/img/logos/PS.png");          // 이브이파킹서비스
        logoMap.put("PW", "/img/logos/PW.png");          // 파워큐브
        logoMap.put("RE", "/img/logos/RE.png");          // 레드이엔지
        logoMap.put("RS", "/img/logos/RS.png");          // 리셀파워
        logoMap.put("S1", "/img/logos/S1.png");          // 에스이피
        logoMap.put("SA", "/img/logos/SA.png");          // 설악에너텍
        logoMap.put("SB", "/img/logos/SB.png");          // 소프트베리
        logoMap.put("SC", "/img/logos/SC.png");          // 삼척시
        logoMap.put("SD", "/img/logos/SD.png");          // 스칼라데이터
        logoMap.put("SE", "/img/logos/SE.png");          // 서울시
        logoMap.put("SF", "/img/logos/SF.png");          // 스타코프
        logoMap.put("SG", "/img/logos/SG.png");          // sk시그넷
        logoMap.put("SH", "/img/logos/SH.png");          // 에스에이치에너지
        logoMap.put("SJ", "/img/logos/SJ.png");          // 세종시
        logoMap.put("SR", "/img/logos/SR.png");          // sk렌터카
        logoMap.put("SL", "/img/logos/SL.png");          // 에스에스기전
        logoMap.put("SM", "/img/logos/SM.png");          // 성민기업
        logoMap.put("SN", "/img/logos/SN.png");          // 서울에너지공사
        logoMap.put("SO", "/img/logos/SO.png");          // 선광시스템
        logoMap.put("SP", "/img/logos/SP.png");          // 스마트포트테크놀로지
        logoMap.put("SS", "/img/logos/SS.png");          // 투이스이브이씨 ==
        logoMap.put("ST", "/img/logos/ST.png");          // sk일렉링크
        logoMap.put("SU", "/img/logos/SU.png");          // 순천시 체육시설관리소
        logoMap.put("SZ", "/img/logos/SZ.png");          // sg생활안전
        logoMap.put("TB", "/img/logos/TB.png");          // 태백시
        logoMap.put("TD", "/img/logos/TD.png");          // 타디스테크놀로지
        logoMap.put("TH", "/img/logos/TH.png");          // 태현교통
        logoMap.put("TL", "/img/logos/TL.png");          // 티엘컴퍼니
        logoMap.put("TM", "/img/logos/TM.png");          // 티맵
        logoMap.put("TR", "/img/logos/TR.png");          // 한마음장애인복지회
        logoMap.put("TS", "/img/logos/TS.png");          // 태성콘텍
        logoMap.put("TU", "/img/logos/TU.png");          // 티비유
        logoMap.put("TV", "/img/logos/TV.png");          // 아이토브
        logoMap.put("UN", "/img/logos/UN.png");          // 유니이브이
        logoMap.put("UP", "/img/logos/UP.png");          // 유플러스아이티
        logoMap.put("US", "/img/logos/US.png");          // 울산시
        logoMap.put("VT", "/img/logos/VT.png");          // 볼타
        logoMap.put("WB", "/img/logos/WB.png");          // 이브이루씨
        logoMap.put("YC", "/img/logos/YC.png");          // 노란충전
        logoMap.put("YY", "/img/logos/YY.png");          // 양양군
        logoMap.put("ZE", "/img/logos/ZE.png");          // 이브이모드코리아

        // ... ⚠️ 나머지 코드는 필요 시 계속 추가


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
        logoMap.put("KL", "/img/logos/skenergy.png");    // 클린일렉스
        logoMap.put("KM", "/img/logos/skenergy.png");    // 카카오모빌리티
        logoMap.put("KN", "/img/logos/skenergy.png");    // 한국환경공단
        logoMap.put("KO", "/img/logos/skenergy.png");    // 이브이파트너스
        logoMap.put("KS", "/img/logos/skenergy.png");    // 이브이씨코리아
        logoMap.put("KT", "/img/logos/skenergy.png");    // 케이티
        logoMap.put("LC", "/img/logos/skenergy.png");    // 롯데건설
        logoMap.put("LD", "/img/logos/skenergy.png");    // 롯데이노베이트
        logoMap.put("LH", "/img/logos/skenergy.png");    // LG유플러스 볼트업
        logoMap.put("LI", "/img/logos/skenergy.png");    // 엘에스이링크
        logoMap.put("LT", "/img/logos/skenergy.png");    // 광성계측기
        logoMap.put("MA", "/img/logos/skenergy.png");    // 맥플러스
        logoMap.put("MO", "/img/logos/skenergy.png");    // 매니지온
        logoMap.put("MR", "/img/logos/skenergy.png");    // 미래씨앤엘
        logoMap.put("MT", "/img/logos/skenergy.png");    // 모던텍
        logoMap.put("NB", "/img/logos/skenergy.png");    // 엔비플러스
        logoMap.put("NE", "/img/logos/skenergy.png");    // 에너넷
        logoMap.put("NH", "/img/logos/skenergy.png");    // 농협경제지주 신재생에너지
        logoMap.put("NS", "/img/logos/skenergy.png");    // 뉴텍솔루션
        logoMap.put("NT", "/img/logos/skenergy.png");    // 한국전자금융
        logoMap.put("NX", "/img/logos/skenergy.png");    // 넥씽
        logoMap.put("OB", "/img/logos/skenergy.png");    // 현대오일뱅크
        logoMap.put("PC", "/img/logos/skenergy.png");    // 파킹클라우드
        logoMap.put("PE", "/img/logos/skenergy.png");    // 피앤이시스템즈
        logoMap.put("PK", "/img/logos/skenergy.png");    // 펌프킨
        logoMap.put("PL", "/img/logos/skenergy.png");    // 플러그링크






    }

    // 정적 접근 메서드
    public static String getLogoUrl(String code) {
        return logoMap.getOrDefault(code, "/img/logos/default.png");
    }

}
