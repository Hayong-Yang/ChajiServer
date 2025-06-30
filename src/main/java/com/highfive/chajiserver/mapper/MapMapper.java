package com.highfive.chajiserver.mapper;

import com.highfive.chajiserver.dto.StationDTO;
import com.highfive.chajiserver.dto.ZscodeMappingDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MapMapper {
    public String getZscode(ZscodeMappingDTO zscodeDTO);
    public List<String> getAllZscode();
    void insertOrUpdateStation(StationDTO dto);
    List<StationDTO> getAllStationsFromDB();
    List<Map<String, String>> getSigunguMap();               // 이름용
    List<Map<String, Object>> getSigunguMapWithCoord();      // 좌표용
    List<Map<String, String>> getSidoMap();                  // 이름용
    List<Map<String, Object>> getSidoMapWithCoord();
}
