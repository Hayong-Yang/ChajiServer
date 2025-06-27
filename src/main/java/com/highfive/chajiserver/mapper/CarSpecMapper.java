package com.highfive.chajiserver.mapper;

import com.highfive.chajiserver.dto.CarSpecDTO;
import com.highfive.chajiserver.dto.TrimDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CarSpecMapper {
    CarSpecDTO findByCarIdx(@Param("carIdx") int carIdx);
    List<String> getAllBrands();
    List<String> getModelsByBrand(@Param("brand") String brand);
    List<TrimDTO> getTrimsByModel(@Param("model") String model);

}
