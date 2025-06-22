package com.highfive.chajiserver.cache;

import com.highfive.chajiserver.dto.StationDTO;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StationCache {
    private final Map<String, StationDTO> cache = new HashMap<>();

    // 새 충전소 데이터를 누적 추가
    public void addAll(List<StationDTO> data) {
        for (StationDTO dto : data) {
            String key = dto.getStatId() + "_" + dto.getChgerId();
            cache.put(key, dto); // 같은 ID는 자동 덮어쓰기 (중복 방지)
        }
    }

    public Collection<StationDTO> getAll() {
        return cache.values(); // 모든 충전소 반환
    }

    public StationDTO get(String stationId) {
        return cache.get(stationId);
    }

    public void clear() {
        cache.clear();
    }

    public boolean contains(String stationId) {
        return cache.containsKey(stationId);
    }



} // class
