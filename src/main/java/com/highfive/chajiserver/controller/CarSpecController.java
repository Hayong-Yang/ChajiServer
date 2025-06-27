package com.highfive.chajiserver.controller;

import com.highfive.chajiserver.dto.TrimDTO;
import com.highfive.chajiserver.mapper.CarSpecMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/car")
@RequiredArgsConstructor
public class CarSpecController {

    private final CarSpecMapper carSpecMapper;

    @GetMapping("/brands")
    public List<String> getBrands() {
        return carSpecMapper.getAllBrands();
    }

    @GetMapping("/models")
    public List<String> getModelsByBrand(@RequestParam String brand) {
        return carSpecMapper.getModelsByBrand(brand);
    }

    @GetMapping("/trims")
    public List<TrimDTO> getTrimsByModel(@RequestParam String model) {
        return carSpecMapper.getTrimsByModel(model);
    }
}