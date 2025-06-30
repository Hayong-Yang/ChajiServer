package com.highfive.chajiserver.controller;

import com.highfive.chajiserver.mapper.CarDataMapper;
import com.highfive.chajiserver.model.CarData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/car")
@RequiredArgsConstructor
public class CarDataController {

    private final CarDataMapper carDataMapper;

    //브랜드 조회
    @GetMapping("/brands")
    public List<String> getBrands() {
        return carDataMapper.getAllBrands();
    }

    //모델 조회
    @GetMapping("/models")
    public List<String> getModels(@RequestParam String brand) {
        return carDataMapper.getModelsByBrand(brand);
    }

    //연도 조회
    @GetMapping("/years")
    public List<Integer> getYears(
            @RequestParam String brand,
            @RequestParam String model
    ) {
        return carDataMapper.getYearsByBrandAndModel(brand, model);
    }

    //트림 조회
    @GetMapping("/trims")
    public List<String> getTrims(
            @RequestParam String brand,
            @RequestParam String model,
            @RequestParam int year
    ) {
        return carDataMapper.getTrimsByBrandModelYear(brand, model, year);
    }

    //커넥터 정보 조회
    @GetMapping("/connectors")
    public Map<String, Boolean> getConnectors(
            @RequestParam String brand,
            @RequestParam String model,
            @RequestParam int year,
            @RequestParam String trim
    ) {
        CarData car = carDataMapper.selectCarDataByDetail(brand, model, year, trim);
        if (car == null) {
            throw new RuntimeException("해당 차량을 찾을 수 없습니다.");
        }

        Map<String, Boolean> connectors = new LinkedHashMap<>();
        connectors.put("AC 완속", car.isConnectorAcSlow());
        connectors.put("DC 콤보", car.isConnectorDcCombo());
        connectors.put("DC 차데모", car.isConnectorDcChademo());
        connectors.put("AC 3상", car.isConnectorAcThreePhase());
        connectors.put("테슬라", car.isConnectorTesla());
        connectors.put("모바일 플러그", car.isConnectorMobilePlug());
        connectors.put("무선 충전", car.isConnectorWireless());

        return connectors;
    }

    //car_data_idx 조회 (등록 시 사용)
    @GetMapping("/car-data-idx")
    public Map<String, Integer> getCarDataIdx(
            @RequestParam String brand,
            @RequestParam String model,
            @RequestParam int year,
            @RequestParam String trim
    ) {
        Integer carDataIdx = carDataMapper.findCarDataIdx(brand, model, year, trim);
        if (carDataIdx == null) {
            throw new RuntimeException("일치하는 차량 정보가 없습니다.");
        }
        return Map.of("carDataIdx", carDataIdx);
    }
}
