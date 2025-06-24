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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.AbstractMap;
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
    public ResponseEntity<?> getStationNear(Map<String, Object> body) {
        try {
            // 안전하게 위.경도 받기
            double lat = extractDouble(body.get("lat"));
            double lon = extractDouble(body.get("lon"));

            // ✅ 프론트에서 전달된 필터값 꺼내기 (기본값 처리 포함)
            boolean freeParking = Boolean.TRUE.equals(body.get("freeParking"));
            boolean noLimit = Boolean.TRUE.equals(body.get("noLimit"));

            List<String> providerList = new ArrayList<>();
            if (body.get("provider") instanceof List) {
                providerList = (List<String>) body.get("provider");
            } else if (body.get("provider") instanceof String) {
                providerList.add(body.get("provider").toString());
            }

            List<String> typeList = new ArrayList<>();
            if (body.get("type") instanceof List) {
                typeList = (List<String>) body.get("type");
            } else if (body.get("type") instanceof String) {
                typeList.add(body.get("type").toString());
            }

            // outputMin/outputMax 필터값 받아오기 (없으면 기본값)
            int outputMin = 0;
            int outputMax = 350;
            if (body.get("outputMin") != null) {
                outputMin = Integer.parseInt(body.get("outputMin").toString());
            }
            if (body.get("outputMax") != null) {
                outputMax = Integer.parseInt(body.get("outputMax").toString());
            }

            System.out.println("백에서 받은 좌표: " + lat + ", " + lon);

            // ✅ 필터 조건 통과한 충전소만 담는 리스트
            List<StationDTO> stationList = new ArrayList<>();

            for (StationDTO station : stationCache.getAll()) {
                if (!geoUtil.isWithinRadius(lat, lon, station.getLat(), station.getLng(), 1000)) continue;
                if ("Y".equalsIgnoreCase(station.getDelYn())) continue;
                if (freeParking && !"Y".equalsIgnoreCase(station.getParkingFree())) continue;
                if (noLimit && ("Y".equalsIgnoreCase(station.getLimitYn()) ||
                        (station.getNote() != null && station.getNote().contains("이용 불가")))) continue;

                double outputValue = 0;
                try {
                    outputValue = Double.parseDouble(station.getOutput().toString());
                } catch (Exception ignore) {}
                if (outputValue < outputMin || outputValue > outputMax) continue;

                if (!typeList.isEmpty() && !typeList.contains(String.valueOf(station.getChgerType()).trim())) continue;
                if (!providerList.isEmpty() && !providerList.contains(station.getBusiId())) continue;

                // ✅ 통과한 충전소만 리스트에 저장
                stationList.add(station);
            }

            // ✅ 리스트에 담긴 충전소들만 점수 계산 및 정렬
            List<Map.Entry<StationDTO, Integer>> scoredList = new ArrayList<>();
            for (StationDTO station : stationList) {
                int score = 0;
                if ("Y".equalsIgnoreCase(station.getParkingFree())) score += 15;
                if ("Y".equalsIgnoreCase(station.getTrafficYn())) score += 10;
                if (station.getUseTime() != null && station.getUseTime().contains("24시간")) score += 10;
                if (station.getStatUpdDt() != null && isWithinLast24Hours(station.getStatUpdDt())) score += 5;
                if (station.getLastTsdt() != null && !station.getLastTsdt().isEmpty()) score += 5;
                if ("Y".equalsIgnoreCase(station.getLimitYn())) score -= 30;

                scoredList.add(new AbstractMap.SimpleEntry<>(station, score));
            }

            // 스코어 내림차순 정렬
            scoredList.sort(Map.Entry.<StationDTO, Integer>comparingByValue().reversed());

            // 3. 결과를 JSON으로 변환
            JSONArray arr = new JSONArray();
            for (Map.Entry<StationDTO, Integer> entry : scoredList) {
                StationDTO station = entry.getKey();
                int score         = entry.getValue();

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

                obj.put("recommendScore", score);
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

    private boolean isWithinLast24Hours(String statUpdDt) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            LocalDateTime updated = LocalDateTime.parse(statUpdDt, fmt);
            return Duration.between(updated, LocalDateTime.now()).toHours() < 24;
        } catch (DateTimeParseException | NullPointerException e) {
            return false;
        }
    }

    // 외부에서 받은 위경도를 double 형으로 변환하는 메소드
    private double extractDouble(Object value) {
        if (value instanceof Number) return ((Number) value).doubleValue();
        if (value instanceof String) return Double.parseDouble((String) value);
        throw new IllegalArgumentException("Invalid number format");
    }

} // class
