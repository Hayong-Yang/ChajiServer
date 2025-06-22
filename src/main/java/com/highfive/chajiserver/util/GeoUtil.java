package com.highfive.chajiserver.util;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class GeoUtil {
    // 지구 반지름 (단위: 미터)
    private static final double EARTH_RADIUS = 6371000;

    // 위도/경도 기준 거리 계산 (Haversine 공식)
    public double calcDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double rLat1 = Math.toRadians(lat1);
        double rLat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(rLat1) * Math.cos(rLat2)
                * Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c; // 거리 (단위: 미터)
    }

    // 기준 반경 이내에 있는지 여부 판단
    public boolean isWithinRadius(double lat1, double lon1, double lat2, double lon2, double radius) {
        return calcDistance(lat1, lon1, lat2, lon2) <= radius;
    }
}
