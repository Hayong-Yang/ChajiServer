package com.highfive.chajiserver.controller;

import com.highfive.chajiserver.dto.ZoomDTO;
import com.highfive.chajiserver.service.StationServiceImpl;
import com.highfive.chajiserver.service.ZoomService;
import com.highfive.chajiserver.util.ZoomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/zoom")
public class ZoomController {

    private final ZoomService zoomService;
    private final StationServiceImpl stationService;
    private final ZoomUtil zoomUtil;

    @GetMapping
    public Object getZoomSummary(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam int zoomLevel
    ) {
        log.info("✅ zoomLevel = {}, lat = {}, lng = {}", zoomLevel, lat, lng);
        if (zoomLevel >= 14) {
            // ✅ 바로 getStationNear() 호출
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("lat", lat);
            requestBody.put("lng", lng);
            requestBody.put("freeParking", false);
            requestBody.put("noLimit", false);
            requestBody.put("outputMin", 0);
            requestBody.put("outputMax", 350);
            requestBody.put("provider", zoomUtil.getAllProviders());
            requestBody.put("type", zoomUtil.getAllChargerTypes());

            ResponseEntity<?> response = stationService.getStationNear(requestBody);
            return response.getBody();  // JSON 배열 형태 반환
        } else {
            return zoomService.getZoomSummary(lat, lng, zoomLevel); // List<ZoomDTO>
        }
    }
}
