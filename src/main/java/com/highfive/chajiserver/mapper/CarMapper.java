package com.highfive.chajiserver.mapper;

import com.highfive.chajiserver.dto.CarDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CarMapper {

    @Select("SELECT * FROM car_spec WHERE car_idx = #{carIdx}")
    CarDTO findById(int caridx);

    @Select("SELECT * FROM car_spec")
    List<CarDTO> findAll();
}
