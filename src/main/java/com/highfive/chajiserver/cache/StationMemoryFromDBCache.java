package com.highfive.chajiserver.cache;

import com.highfive.chajiserver.dto.StationDTO;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StationMemoryFromDBCache {
    private final Map<String, StationDTO> cache = new HashMap<>();

    public void put(StationDTO dto) {
        String key = dto.getStatId() + "_" + dto.getChgerId();
        cache.put(key, dto);
    }

    public void putAll(List<StationDTO> list) {
        for (StationDTO dto : list) put(dto);
    }

    public Map<String, StationDTO> getAll() {
        return cache;
    }

    public Collection<StationDTO> getAllValue() {
        return cache.values();
    }

    public void clear() {
        cache.clear();
    }

}// class
