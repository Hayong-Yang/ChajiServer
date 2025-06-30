package com.highfive.chajiserver.controller;

import com.highfive.chajiserver.dto.MemberCarDTO;
import com.highfive.chajiserver.jwt.JwtUtil;
import com.highfive.chajiserver.model.CarData;
import com.highfive.chajiserver.model.MemberCar;
import com.highfive.chajiserver.service.MemberCarService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member-car")
@RequiredArgsConstructor
public class MemberCarController {
    private final MemberCarService memberCarService;
    private final JwtUtil jwtUtil;

    @GetMapping("/car-data")
    public ResponseEntity<CarData> getCarData(
            @RequestParam String brand,
            @RequestParam String model,
            @RequestParam int year,
            @RequestParam String trim) {
        return ResponseEntity.ok(memberCarService.findCarData(brand, model, year, trim));
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerCar(
            HttpServletRequest request,
            @RequestBody MemberCarDTO dto
    ) {
        int memberIdx = jwtUtil.getUserIdxFromRequest(request);
        dto.setMemberIdx(memberIdx);
        memberCarService.registerCar(dto);
        return ResponseEntity.ok("차량이 등록되었습니다.");
    }

    @GetMapping("/list")
    public ResponseEntity<List<MemberCarDTO>> getMemberCars(HttpServletRequest request) {
        int memberIdx = jwtUtil.getUserIdxFromRequest(request);
        List<MemberCarDTO> carList = memberCarService.getMemberCars(memberIdx);
        return ResponseEntity.ok(carList);
    }

    @DeleteMapping("/delete/{idx}")
    public ResponseEntity<String> deleteMemberCar(
            HttpServletRequest request,
            @PathVariable int idx
    ) {
        int memberIdx = jwtUtil.getUserIdxFromRequest(request);
        memberCarService.deleteMemberCar(memberIdx, idx);
        return ResponseEntity.ok("차량이 삭제되었습니다.");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateMemberCar(
            HttpServletRequest request,
            @RequestBody MemberCarDTO dto
    ) {
        int memberIdx = jwtUtil.getUserIdxFromRequest(request);
        dto.setMemberIdx(memberIdx); // 혹시 프론트에서 안 보낼 경우 대비
        memberCarService.updateMemberCar(dto);
        return ResponseEntity.ok("차량 정보가 수정되었습니다.");
    }
}
