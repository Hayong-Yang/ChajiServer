package com.highfive.chajiserver.service;

import com.highfive.chajiserver.dto.FavoriteStationDTO;
import com.highfive.chajiserver.mapper.FavoriteStationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<FavoriteStationDTO> getFavorites(int memberIdx) {
        return favoriteStationMapper.getFavoriteByMember(memberIdx);
    }

    public boolean isFavorite(int memberIdx, String statId) {
        return favoriteStationMapper.isFavorite(memberIdx, statId);
    }
}
