package com.highfive.chajiserver.util;

import com.highfive.chajiserver.cache.StationMemoryFromDBCache;
import com.highfive.chajiserver.dto.StationDTO;
import com.highfive.chajiserver.mapper.MapMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AllStationsDBUtil {
    private final MapMapper mapper;
    private final ChargerApiUtil chargerApiUtil;
    private final StationMemoryFromDBCache memoryCache;

    public void loadStationsFromDB() {
        memoryCache.clear();
        List<StationDTO> list = mapper.getAllStationsFromDB();
        System.out.println("📦 DB에서 불러온 충전소 개수: " + list.size());
        memoryCache.putAll(list);
        System.out.println("✅ 메모리에 저장된 수: " + memoryCache.getAll().size());
    }

    public int fetchAndStoreAllStations() {
        memoryCache.clear();
        int totalSaved = 0;
        List<String> allZscode = mapper.getAllZscode();

        for (String zscode : allZscode) {
            try {
                List<StationDTO> stations = chargerApiUtil.getStationsByZscode(zscode);
                memoryCache.putAll(stations);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (StationDTO dto : memoryCache.getAll().values()) {
            mapper.insertOrUpdateStation(dto);
            totalSaved++;
        }
        return totalSaved;
    }
    public Collection<StationDTO> getAllStationsFromMemory() {
        return memoryCache.getAll().values();
    }
} // class
