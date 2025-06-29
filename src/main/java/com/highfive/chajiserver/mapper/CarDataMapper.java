package com.highfive.chajiserver.mapper;

import com.highfive.chajiserver.model.CarData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CarDataMapper {
    List<String> getAllBrands();
    List<String> getModelsByBrand(@Param("brand") String brand);
    List<Integer> getYearsByBrandAndModel(String brand, String model);
    List<String> getTrimsByBrandModelYear(String brand, String model, int year);
    CarData selectCarDataByDetail(String brand, String model, int year, String trim);
    Integer findCarDataIdx(String brand, String model, int year, String trim);

}
