package com.highfive.chajiserver.service;

import com.highfive.chajiserver.cache.CompanyLogoCache;
import com.highfive.chajiserver.cache.StationCache;
import com.highfive.chajiserver.cache.StationMemoryFromDBCache;
import com.highfive.chajiserver.dto.LatLngDTO;
import com.highfive.chajiserver.dto.StationDTO;
import com.highfive.chajiserver.util.AllStationsDBUtil;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService {
    private final ChargerApiUtil chargerApiUtil;
    private final ReverseGeo reverseGeo;
    private final StationCache stationCache;
    private final GeoUtil geoUtil;
    private final AllStationsDBUtil allStationsDBUtil;
    private final StationMemoryFromDBCache stationMemoryFromDBCache;

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
            double lat = extractDouble(body.get("lat"));
            double lon = extractDouble(body.get("lon"));

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

            int outputMin = 0;
            int outputMax = 350;
            if (body.get("outputMin") != null) {
                outputMin = Integer.parseInt(body.get("outputMin").toString());
            }
            if (body.get("outputMax") != null) {
                outputMax = Integer.parseInt(body.get("outputMax").toString());
            }

            if (typeList.isEmpty() || providerList.isEmpty()) {
                return ResponseEntity.ok()
                        .header("Content-Type", "application/json; charset=UTF-8")
                        .body("[]");
            }

            // 1. 필터 통과한 충전기 목록
            List<StationDTO> passedList = new ArrayList<>();
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

                if (!typeList.contains(String.valueOf(station.getChgerType()).trim())) continue;
                if (!providerList.contains(station.getBusiId())) continue;

                passedList.add(station);
            }

            // 2. 점수 부여
            List<Map.Entry<StationDTO, Integer>> scoredList = new ArrayList<>();
            for (StationDTO station : passedList) {
                int score = 0;
                if ("Y".equalsIgnoreCase(station.getParkingFree())) score += 15;
                if ("Y".equalsIgnoreCase(station.getTrafficYn())) score += 10;
                if (station.getUseTime() != null && station.getUseTime().contains("24시간")) score += 10;
                if (station.getStatUpdDt() != null && isWithinLast24Hours(station.getStatUpdDt())) score += 5;
                if (station.getLastTsdt() != null && !station.getLastTsdt().isEmpty()) score += 5;
                if ("Y".equalsIgnoreCase(station.getLimitYn())) score -= 30;

                scoredList.add(new AbstractMap.SimpleEntry<>(station, score));
            }

            // 3. statId로 그룹핑
            Map<String, List<Map.Entry<StationDTO, Integer>>> grouped = new HashMap<>();
            for (Map.Entry<StationDTO, Integer> entry : scoredList) {
                StationDTO s = entry.getKey();
                grouped.computeIfAbsent(s.getStatId(), k -> new ArrayList<>()).add(entry);
            }

            // 4. JSON 객체 리스트 생성
            List<JSONObject> resultList = new ArrayList<>();
            for (List<Map.Entry<StationDTO, Integer>> group : grouped.values()) {
                StationDTO rep = group.get(0).getKey();
                int score = group.get(0).getValue();

                JSONObject obj = new JSONObject();
                obj.put("statNm", rep.getStatNm());
                obj.put("statId", rep.getStatId());
                obj.put("addr", rep.getAddr());
                obj.put("lat", rep.getLat());
                obj.put("lng", rep.getLng());
                obj.put("busiId", rep.getBusiId());
                obj.put("bnm", rep.getBnm());
                obj.put("parkingFree", rep.getParkingFree());
                obj.put("recommendScore", score);
                obj.put("logoUrl", CompanyLogoCache.getLogoUrl(rep.getBusiId()));
                obj.put("useTime", rep.getUseTime());

                JSONArray chargers = new JSONArray();
                for (Map.Entry<StationDTO, Integer> entry : group) {
                    StationDTO chg = entry.getKey();
                    JSONObject c = new JSONObject();
                    c.put("chgerId", chg.getChgerId());
                    c.put("chgerType", chg.getChgerType());
                    c.put("stat", chg.getStat());
                    c.put("statUpdDt", chg.getStatUpdDt());
                    c.put("method", chg.getMethod());
                    c.put("output", chg.getOutput());
                    c.put("powerType", chg.getPowerType());
                    c.put("useTime", chg.getUseTime());
                    c.put("lastTsdt", chg.getLastTsdt());
                    c.put("lastTedt", chg.getLastTedt());
                    c.put("nowTsdt", chg.getNowTsdt());
                    chargers.put(c);
                }

                obj.put("chargers", chargers);
                resultList.add(obj);
            }

            // 5. 추천 점수 기준으로 내림차순 정렬
            resultList.sort((a, b) ->
                    Integer.compare(b.getInt("recommendScore"), a.getInt("recommendScore"))
            );

            // 6. JSONArray로 변환
            JSONArray arr = new JSONArray();
            for (JSONObject obj : resultList) {
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

    // 고속도로 필터링
    private boolean shouldIncludeStation(StationDTO station, boolean highwayOnly) {
        if (highwayOnly) {
            try {
                double output = Double.parseDouble(station.getOutput());
                if (output < 50) return false; // 완속이면 제외
            } catch (Exception e) {
                return false; // 파싱 실패도 제외
            }
        }
        return true;
    }

    // 웨이포인트 필터링
    private List<StationDTO> filterStations(List<LatLngDTO> waypoints, double radiusMeters, boolean highwayOnly) {
        allStationsDBUtil.loadStationsFromDB();
        Map<String, StationDTO> allChargers = stationMemoryFromDBCache.getAll();

        // 1. 고속도로 조건 충족하는 충전기만 필터링
        List<StationDTO> filteredChargers = new ArrayList<>();
        for (StationDTO charger : allChargers.values()) {
            if (shouldIncludeStation(charger, highwayOnly)) {
                filteredChargers.add(charger);
            }
        }

        // 2. statId 기준으로 충전기 그룹화
        Map<String, List<StationDTO>> groupedByStation = new HashMap<>();
        for (StationDTO charger : filteredChargers) {
            groupedByStation
                    .computeIfAbsent(charger.getStatId(), k -> new ArrayList<>())
                    .add(charger);
        }

        // 3. 각 충전소마다 대표 충전기 선정 (예: 출력값 높은 순)
        List<StationDTO> representativeStations = new ArrayList<>();
        for (List<StationDTO> chargers : groupedByStation.values()) {
            chargers.sort((a, b) -> {
                try {
                    return Double.compare(
                            Double.parseDouble(b.getOutput()),
                            Double.parseDouble(a.getOutput())
                    );
                } catch (Exception e) {
                    return 0;
                }
            });
            representativeStations.add(chargers.get(0));
        }

        // 4. 웨이포인트 기준 반경 내에 있는 충전소만 선별
//        List<StationDTO> result = new ArrayList<>();
//        for (StationDTO station : representativeStations) {
//            for (LatLngDTO wp : waypoints) {
//                double distance = geoUtil.calcDistance(wp.getLat(), wp.getLng(), station.getLat(), station.getLng());
//                if (distance <= radiusMeters) {
//                    result.add(station);
//                    break;
//                }
//            }
//        }
        int topN = 2; // 웨이포인트에서 거리 가까운 상위 2개 추출
        List<StationDTO> result = new ArrayList<>();

        for (LatLngDTO wp : waypoints) {
            List<StationDTO> nearbyStations = new ArrayList<>();

            for (StationDTO station : representativeStations) {
                double distance = geoUtil.calcDistance(wp.getLat(), wp.getLng(), station.getLat(), station.getLng());
                if (distance <= radiusMeters) {
                    station.setDistance(distance); // StationDTO에 distance 필드가 있다고 가정
                    nearbyStations.add(station);
                }
            }

            // 거리순 정렬 후 상위 N개 추출
            nearbyStations.sort(Comparator.comparingDouble(StationDTO::getDistance));
            for (int i = 0; i < Math.min(topN, nearbyStations.size()); i++) {
                result.add(nearbyStations.get(i));
            }
        }

        return result;
    }
    // 고속도로 전용 - 웨이포인트 기반 충전소 호출 필터링
    @Override
    public List<StationDTO> HighStationsNearWaypoints(List<LatLngDTO> waypoints, double radiusMeters) {
        return filterStations(waypoints, radiusMeters, true);
    }
    // 시내 포함 - 웨이포인트 기반 충전소 호출 필터링
    @Override
    public List<StationDTO> AllStationsNearWaypoints(List<LatLngDTO> waypoints, double radiusMeters) {
        return filterStations(waypoints, radiusMeters, false);
    }

} // class
