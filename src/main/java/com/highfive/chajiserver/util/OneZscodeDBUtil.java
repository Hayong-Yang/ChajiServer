package com.highfive.chajiserver.util;

import com.highfive.chajiserver.dto.StationDTO;
import com.highfive.chajiserver.mapper.MapMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OneZscodeDBUtil {

    private final ChargerApiUtil chargerApiUtil;
    private final MapMapper mapper;

    public int fetchAndStoreOneStations(String zscode) {
        int totalSaved = 0;
        // 1. 공공 API로부터 해당 ZSCODE 지역의 충전소 정보 요청
        List<StationDTO> stationList = chargerApiUtil.getStationsByZscode(zscode);

        // 2. 데이터가 존재할 경우 insertOrUpdate
        if (stationList != null && !stationList.isEmpty()) {
            for (StationDTO station : stationList) {
                mapper.insertOrUpdateStation(station);
                totalSaved++;
            }
        }

        return totalSaved;
    }
} // class
