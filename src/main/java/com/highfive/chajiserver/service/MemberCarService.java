package com.highfive.chajiserver.service;

import com.highfive.chajiserver.dto.MemberCarDTO;
import com.highfive.chajiserver.mapper.MemberCarMapper;
import com.highfive.chajiserver.model.CarData;
import com.highfive.chajiserver.model.MemberCar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberCarService {

    private final MemberCarMapper memberCarMapper;

    //차량 상세 정보 조회 (프론트에서 carDataIdx 조회용)
    public CarData findCarData(String brand, String model, int year, String trim) {
        return memberCarMapper.selectCarDataByDetail(brand, model, year, trim);
    }

    // 차량 등록
    public void registerCar(MemberCarDTO dto) {
        //기존 등록 차량 목록 확인
        List<MemberCar> existingCars = memberCarMapper.selectMemberCars(dto.getMemberIdx());
        boolean isMain = existingCars.isEmpty();

        //MemberCar 객체 생성 및 저장
        MemberCar memberCar = MemberCar.builder()
                .memberIdx(dto.getMemberIdx())
                .carDataIdx(dto.getCarDataIdx())
                .nickname(dto.getNickname())
                .isMain(isMain)
                .build();

        memberCarMapper.insertMemberCar(memberCar);
    }

    public List<MemberCar> getMemberCars(int memberIdx) {
        return memberCarMapper.selectMemberCars(memberIdx);
    }

    public void deleteMemberCar(int memberIdx, int carIdx) {
        // 보안: 해당 사용자의 차량인지 검증 (필수는 아니지만 권장)
        List<MemberCar> cars = memberCarMapper.selectMemberCars(memberIdx);
        boolean ownsCar = cars.stream().anyMatch(c -> c.getIdx() == carIdx);
        if (!ownsCar) {
            throw new RuntimeException("해당 차량은 사용자에게 속해있지 않습니다.");
        }

        memberCarMapper.deleteMemberCar(carIdx);
    }

    public void updateMemberCar(MemberCarDTO dto) {
        // 대표 차량 설정일 경우 기존 대표 차량 초기화
        if (dto.isMain()) {
            memberCarMapper.clearMainCar(dto.getMemberIdx());
        }
        memberCarMapper.updateMemberCar(dto);
    }

}

