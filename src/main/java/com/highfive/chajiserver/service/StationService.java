package com.highfive.chajiserver.service;

import com.highfive.chajiserver.dto.LatLngDTO;
import com.highfive.chajiserver.dto.StationDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface StationService {
    void setStationNear(Map<String, Double> body);
    ResponseEntity<?> getStationNear(Map<String, Object> body);
    List<StationDTO> findStationsNearWaypoints(List<LatLngDTO> waypoints, double radiusMeters );
}
