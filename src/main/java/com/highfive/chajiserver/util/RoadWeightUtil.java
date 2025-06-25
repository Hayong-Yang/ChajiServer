package com.highfive.chajiserver.util;

public class RoadWeightUtil {

        // 도심 평균 속도 추정
        public static double estimateCitySpeed(double cityDistance, double totalDistance, double totalTimeSeconds) {
            if (totalDistance == 0 || totalTimeSeconds == 0) return 0.0;
            double cityTime = (cityDistance / totalDistance) * totalTimeSeconds;
            return (cityDistance / 1000.0) / (cityTime / 3600.0); // km/h
        }

        // 회생 제동 보정 (30km/h 미만 속도에서만 보정)
        public static double cityBoost(double speed, double cityEvRatio) {
            return (speed < 30.0) ? cityEvRatio * 1.35 : cityEvRatio;
        }

        // 도로 가중치 계산
        public static double calculateRoadWeight(double cityEvRatio, double highwayEvRatio,
                                                 double cityDistance, double highwayDistance, double totalTimeSeconds) {
            double totalDistance = cityDistance + highwayDistance;
            if (totalDistance == 0) return 0.0;

            double citySpeed = estimateCitySpeed(cityDistance, totalDistance, totalTimeSeconds);
            double adjustedCityEvRatio = cityBoost(citySpeed, cityEvRatio);

            double cityPortion = cityDistance / totalDistance;
            double highwayPortion = highwayDistance / totalDistance;

            return adjustedCityEvRatio * cityPortion + highwayEvRatio * highwayPortion;
        }
    }



