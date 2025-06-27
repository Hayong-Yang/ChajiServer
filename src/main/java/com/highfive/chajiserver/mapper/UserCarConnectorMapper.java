package com.highfive.chajiserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface UserCarConnectorMapper {

    void insertFromCarSpec(@Param("userCarIdx") int userCarIdx,
                           @Param("carIdx") int carIdx);
}
