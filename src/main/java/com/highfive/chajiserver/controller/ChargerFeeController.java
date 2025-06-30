package com.highfive.chajiserver.controller;

import com.highfive.chajiserver.dto.ChargerFeeDTO;
import com.highfive.chajiserver.service.ChargerFeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/charger-fee")
@RequiredArgsConstructor
public class ChargerFeeController {
    private final ChargerFeeService chargerFeeService;

    @GetMapping("/{busiId}")
    public ChargerFeeDTO getFee(@PathVariable String busiId) {
        return chargerFeeService.getFeeByBusiId(busiId);
    }
}


