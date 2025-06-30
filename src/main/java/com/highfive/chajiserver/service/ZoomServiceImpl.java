package com.highfive.chajiserver.service;

import com.highfive.chajiserver.dto.StationDTO;
import com.highfive.chajiserver.dto.ZoomDTO;
import com.highfive.chajiserver.mapper.MapMapper;
import com.highfive.chajiserver.util.AllStationsDBUtil;
import com.highfive.chajiserver.util.ZoomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZoomServiceImpl implements ZoomService {
    private final AllStationsDBUtil allStationsDBUtil;
    private final ZoomUtil zoomUtil;
    private final MapMapper mapMapper;

    @Override
    public List<ZoomDTO> getZoomSummary(double lat, double lng, int zoomLevel) {
        if (allStationsDBUtil.getAllStationsFromMemory().isEmpty()) {
            log.info("⚠️ 캐시 비어 있음 → DB에서 충전소 불러오기");
            allStationsDBUtil.loadStationsFromDB();
            log.info("✅ 메모리 로딩 완료 → 현재 충전소 수: {}", allStationsDBUtil.getAllStationsFromMemory().size());
        }

        List<StationDTO> stations;
        if (zoomLevel <= 13 && zoomLevel >= 11) {
            stations = new ArrayList<>(allStationsDBUtil.getAllStationsFromMemory());
            log.info("🔍 Zoom12~11 - 전국 구 기준 요약");
            return summarizeByZscode(stations);
        } else if (zoomLevel <= 10 && zoomLevel >= 6) {
            stations = new ArrayList<>(allStationsDBUtil.getAllStationsFromMemory());
            log.info("🔍 Zoom8~6 - 전국 시 기준 요약");
            return summarizeByZcode(stations);
        }

        log.warn("⚠️ 처리되지 않은 줌레벨: {}", zoomLevel);
        return List.of();
    }

    private Map<String, String> convertToMap(List<Map<String, String>> list) {
        return list.stream().collect(Collectors.toMap(
                m -> m.get("key"),
                m -> m.get("value")
        ));
    }

    private Map<String, Double> extractLatMap(List<Map<String, Object>> list) {
        return list.stream()
                .filter(m -> m.get("lat") != null)
                .collect(Collectors.toMap(
                        m -> (String) m.get("key"),
                        m -> ((Number) m.get("lat")).doubleValue()
                ));
    }

    private Map<String, Double> extractLonMap(List<Map<String, Object>> list) {
        return list.stream()
                .filter(m -> m.get("lng") != null)
                .collect(Collectors.toMap(
                        m -> (String) m.get("key"),
                        m -> ((Number) m.get("lng")).doubleValue()
                ));
    }

    private List<ZoomDTO> summarizeByZcode(List<StationDTO> stations) {
        Map<String, String> sidoMap = convertToMap(mapMapper.getSidoMap());
        log.info("✅ sidoMap 키 목록: {}", sidoMap.keySet());
        List<Map<String, Object>> sidoRaw = mapMapper.getSidoMapWithCoord(); // lat/lon 포함
        Map<String, Double> latMap = extractLatMap(sidoRaw);
        Map<String, Double> lonMap = extractLonMap(sidoRaw);

        Map<String, List<StationDTO>> grouped = stations.stream()
                .filter(s -> s.getZcode() != null)
                .collect(Collectors.groupingBy(StationDTO::getZcode));

        List<ZoomDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<StationDTO>> entry : grouped.entrySet()) {
            String zcode = entry.getKey();
            List<StationDTO> group = entry.getValue();


            boolean nameMissing = !sidoMap.containsKey(zcode);
            boolean latMissing = !latMap.containsKey(zcode);
            boolean lonMissing = !lonMap.containsKey(zcode);

            if (nameMissing || latMissing || lonMissing) {
                log.warn("⚠️ 누락 정보 발견: zcode={}, nameMissing={}, latMissing={}, lonMissing={}, count={}",
                        zcode, nameMissing, latMissing, lonMissing, group.size());
                continue;
            }


            ZoomDTO dto = new ZoomDTO();
            dto.setSidoName(sidoMap.get(zcode));
            dto.setSigunguName(null);
            dto.setCount(group.size());
            dto.setLat(latMap.get(zcode));
            dto.setLon(lonMap.get(zcode));
            result.add(dto);
        }

        return result;
    }

    private List<ZoomDTO> summarizeByZscode(List<StationDTO> stations) {
        Map<String, String> sigunguMap = convertToMap(mapMapper.getSigunguMap());
        Map<String, String> sidoMap = convertToMap(mapMapper.getSidoMap());
        List<Map<String, Object>> sigunguRaw = mapMapper.getSigunguMapWithCoord(); // lat/lon 포함
        Map<String, Double> latMap = extractLatMap(sigunguRaw);
        Map<String, Double> lonMap = extractLonMap(sigunguRaw);

        Map<String, List<StationDTO>> grouped = stations.stream()
                .filter(s -> s.getZscode() != null)
                .collect(Collectors.groupingBy(StationDTO::getZscode));

        List<ZoomDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<StationDTO>> entry : grouped.entrySet()) {
            String zscode = entry.getKey();
            List<StationDTO> group = entry.getValue();
            StationDTO sample = group.get(0);

            if (sidoMap.get(sample.getZcode()) == null ||  sigunguMap.get(zscode) == null) {
                log.warn("⛔️ 매핑되지 않은 지역: zcode={}, zscode={}, count={}", sample.getZcode(), zscode, group.size());
                continue;
            }

            ZoomDTO dto = new ZoomDTO();
            dto.setSidoName(sidoMap.get(sample.getZcode()));
            dto.setSigunguName(sigunguMap.get(zscode));
            dto.setCount(group.size());
            dto.setLat(latMap.get(zscode));
            dto.setLon(lonMap.get(zscode));
            result.add(dto);
        }

        return result;
    }
}
