package com.highfive.chajiserver.mapper;

import com.highfive.chajiserver.dto.ChargerFeeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChargerFeeMapper {
    ChargerFeeDTO findByBusiId(@Param("busiId") String busiId);
    List<ChargerFeeDTO> getAllFees();
}

