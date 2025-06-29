package com.highfive.chajiserver.mapper;

import com.highfive.chajiserver.dto.FavoriteStationDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoriteStationMapper {

    void insertFavorite(FavoriteStationDTO dto);

    void deleteFavorite(@Param("memberIdx") int memberIdx,
                        @Param("statId") String statId);

    List<FavoriteStationDTO> getFavoriteByMember(int memberIdx);

    boolean isFavorite(@Param("memberIdx") int memberIdx,
                       @Param("statId") String statId);
}

