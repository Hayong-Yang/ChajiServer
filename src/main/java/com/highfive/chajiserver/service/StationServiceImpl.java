package com.highfive.chajiserver.service;

import com.highfive.chajiserver.cache.CompanyLogoCache;
import com.highfive.chajiserver.cache.StationCache;
import com.highfive.chajiserver.cache.StationMemoryFromDBCache;
import com.highfive.chajiserver.dto.LatLngDTO;
import com.highfive.chajiserver.dto.StationDTO;
import com.highfive.chajiserver.dto.StationFilterDTO;
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

    // [화면 위치 기반으로 주변 충전소를 공공 API로 불러와 전역 캐시에 저장]
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

    // [화면 위치 기준 주변 충전소 필터링 후 추천 점수 기반 응답]
    @Override
    public ResponseEntity<?> getStationNear(Map<String, Object> body) {
        try {
            double lat = extractDouble(body.get("lat"));
            double lon = extractDouble(body.get("lon"));

            System.out.println("✅ 위도: " + lat + ", 경도: " + lon);

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
            // 개발 전용 정적 충전소 데이터
//            for (StationDTO station : stationMemoryFromDBCache.getAllValue()) {
            // 실시간 데이터 전용!!!!!
            for (StationDTO station : stationCache.getAll()) {

                if (!geoUtil.isWithinRadius(lat, lon, station.getLat(), station.getLng(), 1000)) continue;
                if ("Y".equalsIgnoreCase(station.getDelYn())) continue;
                if (freeParking && !"Y".equalsIgnoreCase(station.getParkingFree())) continue;
                if (noLimit && ("Y".equalsIgnoreCase(station.getLimitYn()) ||
                        (station.getNote() != null && station.getNote().contains("이용 불가")))) continue;

                double outputValue = 0;
                try {
                    outputValue = Double.parseDouble(station.getOutput().toString());
                } catch (Exception ignore) {
                }
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
                obj.put("busiCall", rep.getBusiCall());
                obj.put("limitDetail", rep.getLimitDetail());

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

    // [ 웨이포인트 기반 충전소 필터링 핵심 로직]
    private List<StationDTO> filterStations(List<LatLngDTO> waypoints, double radiusMeters, boolean highwayOnly, StationFilterDTO filter) {
        // 1. DB에서 모든 충전소를 메모리로 로드_ 나중에 앱 실행될때 로드해두도록 바꿀것임
//        allStationsDBUtil.loadStationsFromDB();
        Map<String, StationDTO> allChargers = stationMemoryFromDBCache.getAll();

        // 2. 사용자 필터 조건 추출
        boolean freeParking = filter.isFreeParking();
        boolean noLimit = filter.isNoLimit();
        int outputMin = filter.getOutputMin();
        int outputMax = filter.getOutputMax();
        String priority = filter.getPriority();
        List<String> typeList = Optional.ofNullable(filter.getType()).orElse(Collections.emptyList());
        List<String> providerList = Optional.ofNullable(filter.getProvider()).orElse(Collections.emptyList());

        // 3. 필터링: 조건에 맞는 충전기만 추출
        List<StationDTO> filteredChargers = allChargers.values().stream()
                .filter(c -> shouldIncludeStation(c, highwayOnly)) // 고속도로이면 급속만 허용
                .filter(c -> !"Y".equalsIgnoreCase(c.getDelYn())) // 삭제된 충전기 제외
                .filter(c -> !freeParking || "Y".equalsIgnoreCase(c.getParkingFree()))
                .filter(c -> !noLimit || (!"Y".equalsIgnoreCase(c.getLimitYn()) && (c.getNote() == null || !c.getNote().contains("이용 불가"))))
                .filter(c -> {
                    try {
                        double o = Double.parseDouble(c.getOutput());
                        return o >= outputMin && o <= outputMax;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .filter(c -> typeList.isEmpty() || typeList.contains(String.valueOf(c.getChgerType()).trim()))
                .filter(c -> providerList.isEmpty() || providerList.contains(c.getBusiId()))
                .toList();

        // 4. 충전소(statId)별로 그룹핑
        Map<String, List<StationDTO>> groupedByStation = new HashMap<>();
        for (StationDTO charger : filteredChargers) {
            groupedByStation.computeIfAbsent(charger.getStatId(), k -> new ArrayList<>()).add(charger);
        }

        // 5. 웨이포인트마다 반경 내 대표 충전소 추출 (속도 기준으로 정렬)
        Map<LatLngDTO, List<StationDTO>> wpToTopStations = new LinkedHashMap<>();
        for (LatLngDTO wp : waypoints) {
            List<StationDTO> nearbyReps = new ArrayList<>();

            for (List<StationDTO> chargers : groupedByStation.values()) {
                chargers.sort(Comparator.comparingDouble(c -> -Double.parseDouble(c.getOutput())));
                StationDTO rep = chargers.get(0); // 대표 충전기
                double dist = geoUtil.calcDistance(wp.getLat(), wp.getLng(), rep.getLat(), rep.getLng());
                if (dist <= radiusMeters) {
                    rep.setDistance(dist); // 거리 저장
                    nearbyReps.add(rep);
                }
            }
            // 웨이포인트별 대표 충전소 중 상위 5개 추출 (사용자 선호 기반 점수순)
            List<StationDTO> top5 = nearbyReps.stream()
                    .sorted((a, b) -> Integer.compare(
                            calculateWeightedSingleScore(b, priority),
                            calculateWeightedSingleScore(a, priority)))
                    .limit(5)
                    .toList();

            wpToTopStations.put(wp, top5);
        }

        // 6. 웨이포인트 구간을 5개 영역(zone)으로 나누어 충전소 분배
        int totalPoints = waypoints.size();
        int segment = Math.max(1, totalPoints / 5);
        Set<String>[] zones = new Set[]{new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>()};

        for (int i = 0; i < totalPoints; i++) {
            LatLngDTO wp = waypoints.get(i);
            List<StationDTO> stations = wpToTopStations.getOrDefault(wp, new ArrayList<>());
            int zoneIndex = Math.min(i / segment, 4);
            for (StationDTO s : stations) zones[zoneIndex].add(s.getStatId());
        }

        // 7. 각 zone별 충전소 중 대표 충전소 선정 + 점수 높은 충전소 2개 추출 + statId 중복시 다시 뽑기
//        List<StationDTO> result = new ArrayList<>();
//        for (Set<String> zone : zones) {
//            List<StationDTO> zoneList = zone.stream()
//                    .map(groupedByStation::get) // 충전소 전체 리스트
//                    .filter(Objects::nonNull)
//                    .map(list -> list.stream()  // 해당 충전소 중 출력 높은 대표 1개
//                            .max(Comparator.comparingDouble(c -> safeParseOutput(c.getOutput())))
//                            .orElse(null))
//                    .filter(Objects::nonNull)
//                    .toList();
//            // zone별 상위 2개 충전소 추천
//            result.addAll(selectTopStationsByScore(zoneList, groupedByStation, 2, priority));
//        }
        List<StationDTO> result = new ArrayList<>();
        Set<String> addedStatIds = new HashSet<>();

        for (Set<String> zone : zones) {
            List<StationDTO> zoneList = zone.stream()
                    .map(groupedByStation::get)
                    .filter(Objects::nonNull)
                    .map(list -> list.stream()
                            .max(Comparator.comparingDouble(c -> safeParseOutput(c.getOutput())))
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .toList();

            // 점수순 정렬
            List<StationDTO> topByScore = selectTopStationsByScore(zoneList, groupedByStation, zoneList.size(), priority);

            int added = 0;
            for (StationDTO s : topByScore) {
                if (added >= 2) break;
                if (!addedStatIds.contains(s.getStatId())) {
                    result.add(s);
                    addedStatIds.add(s.getStatId());
                    added++;
                }
            }
        }

        // 8. 전체 추천 충전소 반환
        return result;
    }

    private int calculateWeightedSingleScore(StationDTO rep, String priority) {
        int speedScore = 0, reliabilityScore = 0, comfortScore = 0;
        try {
            double output = Double.parseDouble(rep.getOutput());
            speedScore = output >= 200 ? 6 : output >= 100 ? 5 : output >= 50 ? 4 : output >= 7 ? 2 : 1;
        } catch (Exception ignored) {
        }

        if (rep.getStatUpdDt() != null && isWithinLast24Hours(rep.getStatUpdDt())) reliabilityScore += 3;
        if ("Y".equalsIgnoreCase(rep.getParkingFree())) comfortScore += 2;
        if ("Y".equalsIgnoreCase(rep.getTrafficYn())) comfortScore += 2;
        if (rep.getUseTime() != null && rep.getUseTime().contains("24시간")) comfortScore += 2;

        return switch (priority) {
            case "speed" -> speedScore * 2 + reliabilityScore + comfortScore;
            case "reliability" -> speedScore + reliabilityScore * 2 + comfortScore;
            case "comfort" -> speedScore + reliabilityScore + comfortScore * 2;
            default -> speedScore + reliabilityScore + comfortScore;
        };
    }

    private int calculateWeightedStationScore(List<StationDTO> chargers, String priority) {
        if (chargers == null || chargers.isEmpty()) return 0;
        StationDTO rep = chargers.get(0);
        int speedScore = 0, reliabilityScore = 0, comfortScore = 0;

        try {
            double output = Double.parseDouble(rep.getOutput());
            speedScore = output >= 200 ? 6 : output >= 100 ? 5 : output >= 50 ? 4 : output >= 7 ? 2 : 1;
        } catch (Exception ignored) {
        }

        int chargerCount = chargers.size();
        int maxChargerThreshold = 15;
        reliabilityScore += Math.min((chargerCount * 5) / maxChargerThreshold, 5);
        if (rep.getStatUpdDt() != null && isWithinLast24Hours(rep.getStatUpdDt())) reliabilityScore += 2;

        if ("Y".equalsIgnoreCase(rep.getParkingFree())) comfortScore += 2;
        if ("Y".equalsIgnoreCase(rep.getTrafficYn())) comfortScore += 2;
        if (rep.getUseTime() != null && rep.getUseTime().contains("24시간")) comfortScore += 3;

        return switch (priority) {
            case "speed" -> speedScore * 2 + reliabilityScore + comfortScore;
            case "reliability" -> speedScore + reliabilityScore * 2 + comfortScore;
            case "comfort" -> speedScore + reliabilityScore + comfortScore * 2;
            default -> speedScore + reliabilityScore + comfortScore;
        };
    }

    private List<StationDTO> selectTopStationsByScore(List<StationDTO> reps, Map<String, List<StationDTO>> grouped, int limit, String priority) {
        return reps.stream()
                .distinct()
                .sorted((a, b) -> Integer.compare(
                        calculateWeightedStationScore(grouped.get(b.getStatId()), priority),
                        calculateWeightedStationScore(grouped.get(a.getStatId()), priority)))
                .limit(limit)
                .toList();
    }

    private double safeParseOutput(String output) {
        try {
            return Double.parseDouble(output);
        } catch (Exception e) {
            return 0.0;
        }
    }

    // 고속도로 전용 - 웨이포인트 기반 충전소 호출 필터링
    @Override
    public List<StationDTO> HighStationsNearWaypoints(List<LatLngDTO> waypoints, double radiusMeters, StationFilterDTO filter) {
        return filterStations(waypoints, radiusMeters, true, filter);
    }

    // 시내 포함 - 웨이포인트 기반 충전소 호출 필터링
    @Override
    public List<StationDTO> AllStationsNearWaypoints(List<LatLngDTO> waypoints, double radiusMeters, StationFilterDTO filter) {
        return filterStations(waypoints, radiusMeters, false, filter);
    }
}
