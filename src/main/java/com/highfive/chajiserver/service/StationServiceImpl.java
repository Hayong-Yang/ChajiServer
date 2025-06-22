package com.highfive.chajiserver.service;

import com.highfive.chajiserver.cache.CompanyLogoCache;
import com.highfive.chajiserver.cache.StationCache;
import com.highfive.chajiserver.dto.StationDTO;
import com.highfive.chajiserver.util.ChargerApiUtil;
import com.highfive.chajiserver.util.GeoUtil;
import com.highfive.chajiserver.util.ReverseGeo;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService {
    private final ChargerApiUtil chargerApiUtil;
    private final ReverseGeo reverseGeo;
    private final StationCache stationCache;
    private final GeoUtil geoUtil;


    @Override
    public void setStationNear(Map<String, Double> body) {
        try {
            double lat = body.get("lat");
            double lon = body.get("lon");

            // 1. zscode 얻기
            String zscode = reverseGeo.getZscode(lat, lon); // 기존처럼 사용

            // 2. 공공 API에서 충전소 목록 조회
            List<StationDTO> list = chargerApiUtil.getStationsByZscode(zscode);

            // 3. 전역 캐시에 저장
            stationCache.addAll(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResponseEntity<?> getStationNear(Map<String, Double> body) {
        try {
            // 안전하게 위.경도 받기
            double lat = extractDouble(body.get("lat"));
            double lon = extractDouble(body.get("lon"));

            System.out.println("백에서 받은 좌표: " + lat + ", " + lon);

            // 2. 캐시에서 충전소 리스트 가져와 반경 필터링
            List<StationDTO> allStations = new ArrayList<>(stationCache.getAll());
            List<StationDTO> nearby = new ArrayList<>();

            for (StationDTO station : allStations) {
                if (geoUtil.isWithinRadius(lat, lon, station.getLat(), station.getLng(), 1000)) {
                    nearby.add(station);
                }
            }
            // 3. 결과를 JSON으로 변환
            JSONArray arr = new JSONArray();
            for (StationDTO station : nearby) {
                JSONObject obj = new JSONObject();
                obj.put("statNm", station.getStatNm());
                obj.put("statId", station.getStatId());
                obj.put("chgerId", station.getChgerId());
                obj.put("chgerType", station.getChgerType());
                obj.put("addr", station.getAddr());
                obj.put("addrDetail", station.getAddrDetail());
                obj.put("location", station.getLocation());
                obj.put("useTime", station.getUseTime());
                obj.put("lat", station.getLat());
                obj.put("lng", station.getLng());
                obj.put("busiId", station.getBusiId());
                obj.put("bnm", station.getBnm());
                obj.put("busiNm", station.getBusiNm());
                obj.put("busiCall", station.getBusiCall());
                obj.put("stat", station.getStat());
                obj.put("statUpdDt", station.getStatUpdDt());
                obj.put("lastTsdt", station.getLastTsdt());
                obj.put("lastTedt", station.getLastTedt());
                obj.put("powerType", station.getPowerType());
                obj.put("output", station.getOutput());
                obj.put("method", station.getMethod());
                obj.put("zcode", station.getZcode());
                obj.put("zscode", station.getZscode());
                obj.put("kind", station.getKind());
                obj.put("kindDetail", station.getKindDetail());
                obj.put("parkingFree", station.getParkingFree());
                obj.put("note", station.getNote());
                obj.put("limitYn", station.getLimitYn());
                obj.put("limitDetail", station.getLimitDetail());
                obj.put("delYn", station.getDelYn());
                obj.put("delDetail", station.getDelDetail());
                obj.put("trafficYn", station.getTrafficYn());
                obj.put("year", station.getYear());
                obj.put("logoUrl", CompanyLogoCache.getLogoUrl(station.getBusiId()));

                arr.put(obj);
            }

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .body(arr.toString());


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"Internal Server Error\"}");
        }
    }

    // 외부에서 받은 위경도를 double 형으로 변환하는 메소드
    private double extractDouble(Object value) {
        if (value instanceof Number) return ((Number) value).doubleValue();
        if (value instanceof String) return Double.parseDouble((String) value);
        throw new IllegalArgumentException("Invalid number format");
    }

} // class
