package com.highfive.chajiserver.mapper;

import com.highfive.chajiserver.dto.ZscodeMappingDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MapMapper {
    public String getZscode(ZscodeMappingDTO zscodeDTO);
}
