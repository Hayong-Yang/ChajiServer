package com.highfive.chajiserver.mapper;

import com.highfive.chajiserver.dto.MemberCarDTO;
import com.highfive.chajiserver.model.CarData;
import com.highfive.chajiserver.model.MemberCar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberCarMapper {

    // 기존 차량 정보 조회
    CarData selectCarDataByDetail(
            @Param("brand") String brand,
            @Param("model") String model,
            @Param("year") int year,
            @Param("trim") String trim
    );

    // car_data_idx만 조회 (등록용)
    int findCarDataIdx(
            @Param("brand") String brand,
            @Param("model") String model,
            @Param("year") int year,
            @Param("trim") String trim
    );

    // 등록된 차량 리스트
    List<MemberCar> selectMemberCars(int memberIdx);

    // 등록된 차량 수 (대표 여부 판단용)
    int countByMemberIdx(int memberIdx);

    // member_car 테이블 insert
    void insertMemberCar(MemberCar memberCar);

    void deleteMemberCar(int idx);

    void updateMemberCar(MemberCarDTO dto);

    void clearMainCar(int memberIdx); // 대표 초기화
}
