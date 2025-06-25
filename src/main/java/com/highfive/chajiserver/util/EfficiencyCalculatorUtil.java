package com.highfive.chajiserver.util;

import com.highfive.chajiserver.dto.CarDTO;
import com.highfive.chajiserver.mapper.CarMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class EfficiencyCalculatorUtil {

    private final CarMapper carMapper;

    public EfficiencyCalculatorUtil(CarMapper carMapper) {
        this.carMapper = carMapper;
    }

    public double calculate(int carIdx,
                            LocalDate date,
                            double cityEvRatio, double highwayEvRatio,
                            double cityDistance, double highwayDistance, double totalTimeSeconds) {

        CarDTO car = carMapper.findById(carIdx);
        if (car == null) {
            throw new IllegalArgumentException("차량 정보 없음: " + carIdx);
        }

        double baseEfficiency = car.getEfficiency();

        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        double temperature = TempWeightUtil.getInterpolatedTemperature(month, day);
        double tempWeight = TempWeightUtil.getTemperatureWeight(temperature);

        double roadWeight = RoadWeightUtil.calculateRoadWeight(
                cityEvRatio, highwayEvRatio,
                cityDistance, highwayDistance, totalTimeSeconds
        );

        double result = baseEfficiency * tempWeight * roadWeight;

        System.out.printf("[전비 계산] base: %.2f, tempW: %.2f, roadW: %.2f → result: %.2f\n",
                baseEfficiency, tempWeight, roadWeight, result);

        return result;
    }
}
