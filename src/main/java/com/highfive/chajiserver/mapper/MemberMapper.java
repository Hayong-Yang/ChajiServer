package com.highfive.chajiserver.mapper;

import com.highfive.chajiserver.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {
    void save(MemberDTO member);
    MemberDTO findByUserId(@Param("userId") String userId);
    void update(MemberDTO member);
}
