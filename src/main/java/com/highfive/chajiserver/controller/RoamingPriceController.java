package com.highfive.chajiserver.controller;

import com.highfive.chajiserver.dto.RoamingPriceDTO;
import com.highfive.chajiserver.service.RoamingPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/roaming-price")
@RequiredArgsConstructor
public class RoamingPriceController {

    private final RoamingPriceService roamingPriceService;

    @GetMapping
    public RoamingPriceDTO getFeeInfo(
            @RequestParam String memberCompany,
            @RequestParam String operatorCode) {

        return roamingPriceService.getFeeInfo(memberCompany, operatorCode);
    }
}

