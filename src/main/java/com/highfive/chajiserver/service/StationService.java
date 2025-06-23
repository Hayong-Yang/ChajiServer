package com.highfive.chajiserver.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface StationService {
    void setStationNear(Map<String, Double> body);
    ResponseEntity<?> getStationNear(Map<String, Object> body);
}
