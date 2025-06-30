package com.highfive.chajiserver.mapper;

import com.highfive.chajiserver.dto.FavoriteStationDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface FavoriteStationMapper {

    void insertFavorite(FavoriteStationDTO dto);

    void deleteFavorite(@Param("memberIdx") int memberIdx,
                        @Param("statId") String statId);

    boolean isFavorite(@Param("memberIdx") int memberIdx,
                       @Param("statId") String statId);

    // 즐겨찾기 정보 + 충전소 상세 조인
    List<Map<String, Object>> getFavoritesWithStationInfo(int memberIdx);
}

