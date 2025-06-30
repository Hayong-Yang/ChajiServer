package com.highfive.chajiserver.controller;

import com.highfive.chajiserver.dto.LatLngDTO;
import com.highfive.chajiserver.dto.RouteRequestDTO;
import com.highfive.chajiserver.dto.StationDTO;
import com.highfive.chajiserver.dto.StationFilterDTO;
import com.highfive.chajiserver.service.StationService;
import com.highfive.chajiserver.util.AllStationsDBUtil;
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
    private final AllStationsDBUtil allStationsDBUtil;

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
        StationFilterDTO dto = new StationFilterDTO();

        double radiusMeters  = 5000; // 10km: 10000
        List<LatLngDTO> waypoints = request.getWaypoints();
        boolean highway = request.isHighway();

        dto.setFreeParking(request.isFreeParking());
        dto.setNoLimit(request.isNoLimit());
        dto.setOutputMin(request.getOutputMin());
        dto.setOutputMax(request.getOutputMax());
        dto.setType(request.getType());
        dto.setProvider(request.getProvider());
        dto.setPriority(request.getPriority());

        List<StationDTO> stations;
        if (highway) {
            // 고속도로 경로: 급속 전용 충전소만
            stations = service.HighStationsNearWaypoints(waypoints, radiusMeters, dto);
        } else {
            // 도심 경로: 완속 포함
            stations = service.AllStationsNearWaypoints(waypoints, radiusMeters, dto);
        }
        return ResponseEntity.ok(stations);
    }

    @PostMapping("/cache/loadAllStations")
    public ResponseEntity<?> loadAllStationsToCache() {
        try {
            allStationsDBUtil.loadStationsFromDB();
            return ResponseEntity.ok("전국 충전소 데이터를 캐시에 적재했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("충전소 캐시 적재 실패");
        }
    }


} // class
