package com.highfive.chajiserver.util;

import com.highfive.chajiserver.dto.LatLngDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class TmapUtil {
    private final RestTemplate restTemplate;

    // Tmap 경유지 포함 시간 계산
    public long getDetourTimeInSeconds(LatLngDTO start, LatLngDTO waypoint, LatLngDTO end) {
        try {
            // 1. 기본 경로 시간
            long baseTime = getRouteTimeInSeconds(start, end);

            // 2. 경유지 포함 경로 시간
            long detourTime = getRouteTimeInSeconds(start, waypoint, end);

            // 3. 우회 시간 반환
            return detourTime - baseTime;

        } catch (Exception e) {
            e.printStackTrace();
            return Long.MAX_VALUE; // 실패 시 큰 값 반환
        }
    }

    // 기본 경로 요청 (start → end) or (start → waypoint → end)
    public long getRouteTimeInSeconds(LatLngDTO... points) {
        // Tmap API 요청 URL 구성
        String url = "https://apis.openapi.sk.com/tmap/routes?version=1&format=json";
        String appKey = "rzCNpiuhIX5l0dwT9rvQ93GRc22mFn6baRSvJYFl"; // 환경변수로 옮기기

        // 요청 바디 구성
        String body = buildTmapRequestBody(points);

        try {
            // HTTP 요청
            String response = restTemplate.postForObject(url, buildHttpEntity(body, appKey), String.class);

            // 응답 파싱 (예시)
            return parseTotalTimeFromTmapResponse(response);

        } catch (Exception e) {
            e.printStackTrace();
            return Long.MAX_VALUE;
        }
    }

    // [도우미] 요청 바디 JSON 생성
    private String buildTmapRequestBody(LatLngDTO... points) {
        // JSON 문자열 수동 조립 또는 ObjectMapper 사용 가능
        // 경유지 있을 경우 waypoints 형식 포함
        // 예시 단순화
        return "{...}";
    }

    // [도우미] 응답에서 총 시간 추출
    private long parseTotalTimeFromTmapResponse(String response) {
        // 실제 JSON 응답에서 totalTime(초 단위) 추출
        // 예시: new JSONObject(response).getJSONObject("features").getLong("totalTime")
        return 600; // 예시: 600초 = 10분
    }

    // [도우미] HTTP 요청 헤더 포함 구성
    private org.springframework.http.HttpEntity<String> buildHttpEntity(String body, String appKey) {
        var headers = new org.springframework.http.HttpHeaders();
        headers.set("appKey", appKey);
        headers.set("Content-Type", "application/json");
        return new org.springframework.http.HttpEntity<>(body, headers);
    }

} // class
