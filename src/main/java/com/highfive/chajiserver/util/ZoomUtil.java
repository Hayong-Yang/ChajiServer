package com.highfive.chajiserver.util;

import com.highfive.chajiserver.cache.StationCache;
import com.highfive.chajiserver.cache.StationMemoryFromDBCache;
import com.highfive.chajiserver.dto.StationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ZoomUtil {

    private final StationMemoryFromDBCache memoryCache;
    private final GeoUtil geoUtil;

    // ✅ 단순 반경 필터링 함수
    public List<StationDTO> ZoomByRadius(double lat, double lng, int radiusMeters) {
        log.info("📌 ZoomByRadius 호출 - 중심: ({}, {}), 반경: {}m", lat, lng, radiusMeters);
        log.info("✅ ZoomByRadius 호출 시점의 memoryCache 크기: {}", memoryCache.getAll().size());
        List<StationDTO> all = new ArrayList<>(memoryCache.getAll().values());
        log.info("📌 캐시 내 총 충전소 수: {}", all.size());


        return all.stream()
                .filter(s -> geoUtil.isWithinRadius(lat, lng, s.getLat(), s.getLng(), radiusMeters))
                .filter(s -> !"Y".equalsIgnoreCase(s.getDelYn()))
                .collect(Collectors.toList());
    }



    public List<String> getAllProviders() {
        return memoryCache.getAll().values().stream()
                .map(StationDTO::getBusiId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getAllChargerTypes() {
        return memoryCache.getAll().values().stream()
                .map(s -> String.valueOf(s.getChgerType()).trim())
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }
}
