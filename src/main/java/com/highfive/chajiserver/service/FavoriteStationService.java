package com.highfive.chajiserver.service;

import com.highfive.chajiserver.dto.FavoriteStationDTO;
import com.highfive.chajiserver.mapper.FavoriteStationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FavoriteStationService {

    private final FavoriteStationMapper favoriteStationMapper;

    public void addFavorite(FavoriteStationDTO dto) {
        favoriteStationMapper.insertFavorite(dto);
    }

    public void deleteFavorite(int memberIdx, String stationId) {
        favoriteStationMapper.deleteFavorite(memberIdx, stationId);
    }

    public boolean isFavorite(int memberIdx, String statId) {
        return favoriteStationMapper.isFavorite(memberIdx, statId);
    }

    public List<Map<String, Object>> getFavoritesWithDetail(int memberIdx) {
        return favoriteStationMapper.getFavoritesWithStationInfo(memberIdx);
    }
}
