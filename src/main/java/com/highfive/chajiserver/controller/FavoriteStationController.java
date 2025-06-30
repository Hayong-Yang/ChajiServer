package com.highfive.chajiserver.controller;

import com.highfive.chajiserver.dto.FavoriteStationDTO;
import com.highfive.chajiserver.jwt.JwtUtil;
import com.highfive.chajiserver.service.FavoriteStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteStationController {

    private final FavoriteStationService favoriteStationService;
    private final JwtUtil jwtUtil;

    // 즐겨찾기 추가
    @PostMapping
    public void addFavorite(@RequestBody FavoriteStationDTO dto, HttpServletRequest request) {
        int memberIdx = jwtUtil.getUserIdxFromRequest(request);
        dto.setMemberIdx(memberIdx);
        favoriteStationService.addFavorite(dto);
    }

    // 즐겨찾기 삭제
    @DeleteMapping("/delete")
    public void deleteFavorite(@RequestParam String statId, HttpServletRequest request) {
        int memberIdx = jwtUtil.getUserIdxFromRequest(request);
        favoriteStationService.deleteFavorite(memberIdx, statId);
    }

    // 즐겨찾기 목록 조회
    @GetMapping("/list")
    public List<Map<String, Object>> getFavoriteList(HttpServletRequest request) {
        int memberIdx = jwtUtil.getUserIdxFromRequest(request);
        return favoriteStationService.getFavoritesWithDetail(memberIdx);
    }

    // 즐겨찾기 여부 확인
    @GetMapping("/check")
    public boolean isFavorite(@RequestParam String statId, HttpServletRequest request) {
        int memberIdx = jwtUtil.getUserIdxFromRequest(request);
        return favoriteStationService.isFavorite(memberIdx, statId);
    }
}
