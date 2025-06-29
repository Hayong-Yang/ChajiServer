package com.highfive.chajiserver.mapper;

import com.highfive.chajiserver.dto.RoamingPriceDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoamingPriceMapper {
    RoamingPriceDTO getFeeInfo(
            @Param("memberCompany") String memberCompany,
            @Param("operatorCode") String operatorCode
    );
}


