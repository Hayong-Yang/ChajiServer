package com.highfive.chajiserver.controller;

import com.highfive.chajiserver.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("api/station")
@RequiredArgsConstructor
public class StationController {
    private final StationService service;

    @PostMapping("/setStationNear")
    public ResponseEntity<?> setStationNear(@RequestBody Map<String, Double> body) {
        try {
            service.setStationNear(body);
            return ResponseEntity.ok("충전소 정보 캐싱 완료");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("setStationNear 오류 발생");
        }
    }

    @PostMapping("getStationNear")
    public ResponseEntity<?> getStationNear(@RequestBody Map<String, Double> body) {
        try{
           return service.getStationNear(body);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("getStationNear 오류 발생");
        }

    }




} // class
