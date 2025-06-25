package com.highfive.chajiserver.controller;

import com.highfive.chajiserver.dto.LatLngDTO;
import com.highfive.chajiserver.util.AllStationsDBUtil;
import com.highfive.chajiserver.util.OneZscodeDBUtil;
import com.highfive.chajiserver.util.ReverseGeo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/master")
@RequiredArgsConstructor
public class MasterController {
    private final AllStationsDBUtil allStationUpdate;
    private final OneZscodeDBUtil oneZscodeUpdate;
    private final ReverseGeo reverseGeo;

    @PostMapping("/updateAllStations")
    public ResponseEntity<?> updateAllStations() {
        try {
            int updatedCount = allStationUpdate.fetchAndStoreAllStations();
            return ResponseEntity.ok(updatedCount + "개의 충전소 정보 갱신 완료!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("⚠️ 충전소 갱신 중 오류 발생");
        }
    }

    @PostMapping("/updateOnlyOneZscode")
    public ResponseEntity<?> updateOnlyOneZscode(@RequestBody Map<String, String> payload) {
        try {
            String zscode = payload.get("zscode");
            int updatedCount = oneZscodeUpdate.fetchAndStoreOneStations(zscode);
            return ResponseEntity.ok(updatedCount + "개의 충전소 정보 갱신 완료!");
        } catch (Exception e) {
            e.printStackTrace(); // 에러 로그 출력 (선택사항)
            return ResponseEntity.status(500).body("⚠️ 충전소 갱신 중 오류 발생");
        }
    }

    @PostMapping("translateToZscode")
    public ResponseEntity<?> translateToZscode(@RequestBody LatLngDTO latLng) {
        double lat = latLng.getLat();
        double lng = latLng.getLng();
        String zscode = reverseGeo.getZscode(lat, lng); // 변환 로직 호출
        return ResponseEntity.ok(zscode);
    }

} // class
