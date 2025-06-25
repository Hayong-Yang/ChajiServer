package com.highfive.chajiserver.controller;

import com.highfive.chajiserver.dto.LatLngDTO;
import com.highfive.chajiserver.dto.RouteRequestDTO;
import com.highfive.chajiserver.dto.StationDTO;
import com.highfive.chajiserver.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<?> getStationNear(@RequestBody Map<String, Object> body) {
        try{
           return service.getStationNear(body);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("getStationNear 오류 발생");
        }
    }

    @PostMapping("/getStationsNearWaypoints")
    public ResponseEntity<List<StationDTO>> getStationsNearWaypoints(@RequestBody RouteRequestDTO request) {
        double radiusMeters  = 5000; // 10km: 10000
        List<LatLngDTO> waypoints = request.getWaypoints();
        boolean highway = request.isHighway();

        List<StationDTO> stations;
        if (highway) {
            // 고속도로 경로: 급속 전용 충전소만
            stations = service.HighStationsNearWaypoints(waypoints, radiusMeters);
        } else {
            // 도심 경로: 완속 포함
            stations = service.AllStationsNearWaypoints(waypoints, radiusMeters);
        }
        return ResponseEntity.ok(stations);
    }


} // class
