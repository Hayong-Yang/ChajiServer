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

            // 필터 조건 통과한 충전소만 담는 리스트
            List<StationDTO> stationList = new ArrayList<>();

            // 수정된 부분: 타입 또는 사업자 리스트가 비어있으면 아무 결과도 반환하지 않음
            if (typeList.isEmpty() || providerList.isEmpty()) {
                return ResponseEntity.ok()
                        .header("Content-Type", "application/json; charset=UTF-8")
                        .body("[]");
            }

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

    // 🔁 웨이포인트 기반 충전소 필터링 핵심 로직
    private List<StationDTO> filterStations(List<LatLngDTO> waypoints, double radiusMeters, boolean highwayOnly, StationFilterDTO filter) {
        allStationsDBUtil.loadStationsFromDB();
        Map<String, StationDTO> allChargers = stationMemoryFromDBCache.getAll();

        boolean freeParking = filter.isFreeParking();
        boolean noLimit = filter.isNoLimit();
        int outputMin = filter.getOutputMin();
        int outputMax = filter.getOutputMax();
        String priority = filter.getPriority();

        List<String> typeList = Optional.ofNullable(filter.getType()).orElse(Collections.emptyList());
        List<String> providerList = Optional.ofNullable(filter.getProvider()).orElse(Collections.emptyList());

        List<StationDTO> filteredChargers = allChargers.values().stream()
                .filter(c -> shouldIncludeStation(c, highwayOnly))
                .filter(c -> !"Y".equalsIgnoreCase(c.getDelYn()))
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

        Map<String, List<StationDTO>> groupedByStation = new HashMap<>();
        for (StationDTO charger : filteredChargers) {
            groupedByStation.computeIfAbsent(charger.getStatId(), k -> new ArrayList<>()).add(charger);
        }

        Map<LatLngDTO, List<StationDTO>> wpToTopStations = new LinkedHashMap<>();
        for (LatLngDTO wp : waypoints) {
            List<StationDTO> nearbyReps = new ArrayList<>();

            for (List<StationDTO> chargers : groupedByStation.values()) {
                chargers.sort(Comparator.comparingDouble(c -> -Double.parseDouble(c.getOutput())));
                StationDTO rep = chargers.get(0);
                double dist = geoUtil.calcDistance(wp.getLat(), wp.getLng(), rep.getLat(), rep.getLng());
                if (dist <= radiusMeters) {
                    rep.setDistance(dist);
                    nearbyReps.add(rep);
                }
            }

            List<StationDTO> top5 = nearbyReps.stream()
                    .sorted((a, b) -> Integer.compare(
                            calculateWeightedSingleScore(b, priority),
                            calculateWeightedSingleScore(a, priority)))
                    .limit(5)
                    .toList();

            wpToTopStations.put(wp, top5);
        }

        int totalPoints = waypoints.size();
        int segment = Math.max(1, totalPoints / 5);
        Set<String>[] zones = new Set[]{new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>()};

        for (int i = 0; i < totalPoints; i++) {
            LatLngDTO wp = waypoints.get(i);
            List<StationDTO> stations = wpToTopStations.getOrDefault(wp, new ArrayList<>());
            int zoneIndex = Math.min(i / segment, 4);
            for (StationDTO s : stations) zones[zoneIndex].add(s.getStatId());
        }

        List<StationDTO> result = new ArrayList<>();
        for (Set<String> zone : zones) {
            List<StationDTO> zoneList = zone.stream()
                    .map(groupedByStation::get)
                    .filter(Objects::nonNull)
                    .map(list -> list.stream()
                            .max(Comparator.comparingDouble(c -> safeParseOutput(c.getOutput())))
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .toList();

            result.addAll(selectTopStationsByScore(zoneList, groupedByStation, 2, priority));
        }

        return result;
    }

    private int calculateWeightedSingleScore(StationDTO rep, String priority) {
        int speedScore = 0, reliabilityScore = 0, comfortScore = 0;
        try {
            double output = Double.parseDouble(rep.getOutput());
            speedScore = output >= 200 ? 10 : output >= 100 ? 8 : output >= 50 ? 6 : output >= 7 ? 4 : 2;
        } catch (Exception ignored) {}

        if (rep.getStatUpdDt() != null && isWithinLast24Hours(rep.getStatUpdDt())) reliabilityScore += 3;
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

    private int calculateWeightedStationScore(List<StationDTO> chargers, String priority) {
        if (chargers == null || chargers.isEmpty()) return 0;
        StationDTO rep = chargers.get(0);
        int speedScore = 0, reliabilityScore = 0, comfortScore = 0;

        try {
            double output = Double.parseDouble(rep.getOutput());
            speedScore = output >= 200 ? 10 : output >= 100 ? 8 : output >= 50 ? 6 : output >= 7 ? 4 : 2;
        } catch (Exception ignored) {}

        reliabilityScore += chargers.size();
        if (rep.getStatUpdDt() != null && isWithinLast24Hours(rep.getStatUpdDt())) reliabilityScore += 3;

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

} // class
