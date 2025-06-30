package com.highfive.chajiserver.service;

import com.highfive.chajiserver.dto.ZoomDTO;
import java.util.List;
import java.util.Map;

public interface ZoomService {
     /**
      * 줌 레벨 13 이하: 구/시 단위 요약 정보 (ZoomDTO 기반)
      * @param lat 중심 위도
      * @param lon 중심 경도
      * @param zoomLevel 줌 레벨 (13 이하)
      * @return ZoomDTO 리스트 (count 포함한 요약 마커용)
      */
     List<ZoomDTO> getZoomSummary(double lat, double lon, int zoomLevel);
}
