package com.highfive.chajiserver.controller;

import com.highfive.chajiserver.dto.CarRegistDTO;
import com.highfive.chajiserver.service.CarRegistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/car")
@RequiredArgsConstructor
public class CarRegistController {

    private final CarRegistService carRegistService;

    @PostMapping("/register")
    public ResponseEntity<?> registerCar(@RequestBody CarRegistDTO req) {
        try {
            carRegistService.registerCar(req);
            return ResponseEntity.ok("차량 등록 완료!");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
