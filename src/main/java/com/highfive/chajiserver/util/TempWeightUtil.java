package com.highfive.chajiserver.util;

public class TempWeightUtil {

    // 최적 온도 범위 및 감쇠 계수
    private static final double OPTIMAL_TEMP_MIN = 18.0;
    private static final double OPTIMAL_TEMP_MAX = 22.0;
    private static final double ALPHA_WINTER = 0.02;
    private static final double ALPHA_SUMMER = 0.01;
    private static final int BETA = 2;

    // 월별 평균 기온 (서울)
    private static final double[] monthlyAvgTemps = {
            -2.5, 0.3, 5.7, 12.8, 17.9, 22.2,
            25.7, 26.4, 21.9, 15.0, 7.3, 0.4
    };

    private static final int[] daysInMonth = {
            31, 28, 31, 30, 31, 30,
            31, 31, 30, 31, 30, 31
    };

    // 날짜 기준 보간된 온도 계산
    public static double getInterpolatedTemperature(int month, int day) {
        int currentIndex = month - 1;
        int nextIndex = (month == 12) ? 0 : currentIndex + 1;

        double startTemp = monthlyAvgTemps[currentIndex];
        double endTemp = monthlyAvgTemps[nextIndex];
        int daysInCurrentMonth = daysInMonth[currentIndex];

        double ratio = (double) (day - 1) / daysInCurrentMonth;
        double interpolated = startTemp + (endTemp - startTemp) * ratio;

        return Math.round(interpolated * 10.0) / 10.0;
    }

    // 온도 가중치 계산 (18~22도는 보정 없이 1.0로)
    public static double getTemperatureWeight(double temperature) {
        if (temperature >= OPTIMAL_TEMP_MIN && temperature <= OPTIMAL_TEMP_MAX) {
            return 1.0;
        }

        double deviation;
        if (temperature < OPTIMAL_TEMP_MIN) {
            deviation = OPTIMAL_TEMP_MIN - temperature;
            return 1.0 / (1 + ALPHA_WINTER * Math.pow(deviation, BETA));
        } else {
            deviation = temperature - OPTIMAL_TEMP_MAX;
            return 1.0 / (1 + ALPHA_SUMMER * Math.pow(deviation, BETA));
        }
    }
}
