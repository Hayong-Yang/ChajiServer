//package com.highfive.chajiserver.util;
//
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//
//@Component
//public class EfficiencyCalculatorUtil {
//
//    private final CarSpecMapper carMapper;
//
//    public EfficiencyCalculatorUtil(CarSpecMapper carMapper) {
//        this.carMapper = carMapper;
//    }
//
//    public double calculate(int carIdx,
//                            LocalDate date,
//                            double cityEvRatio, double highwayEvRatio,
//                            double cityDistance, double highwayDistance, double totalTimeSeconds) {
//
//        CarSpecDTO car = carMapper.findByCarIdx(carIdx);
//        if (car == null) {
//            throw new IllegalArgumentException("차량 정보 없음: " + carIdx);
//        }
//
//        double baseEfficiency = car.getEvEfficiency();
//
//        int month = date.getMonthValue();
//        int day = date.getDayOfMonth();
//        double temperature = TempWeightUtil.getInterpolatedTemperature(month, day);
//        double tempWeight = TempWeightUtil.getTemperatureWeight(temperature);
//
//        double roadWeight = RoadWeightUtil.calculateRoadWeight(
//                cityEvRatio, highwayEvRatio,
//                cityDistance, highwayDistance, totalTimeSeconds
//        );
//
//        double result = baseEfficiency * tempWeight * roadWeight;
//
//        System.out.printf("[전비 계산] base: %.2f, tempW: %.2f, roadW: %.2f → result: %.2f\n",
//                baseEfficiency, tempWeight, roadWeight, result);
//
//        return result;
//    }
//}
