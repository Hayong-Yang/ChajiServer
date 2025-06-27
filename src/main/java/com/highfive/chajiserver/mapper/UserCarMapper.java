package com.highfive.chajiserver.mapper;

import com.highfive.chajiserver.dto.CarRegistDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserCarMapper {

    int countByMemberIdx(int memberIdx);
    void insertUserCar(CarRegistDTO req); // auto_increment로 저장

}
