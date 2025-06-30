package com.highfive.chajiserver.service;

import com.highfive.chajiserver.dto.RoamingPriceDTO;
import com.highfive.chajiserver.mapper.RoamingPriceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoamingPriceServiceImpl implements RoamingPriceService {

    private final RoamingPriceMapper roamingPriceMapper;

    @Override
    public RoamingPriceDTO getFeeInfo(String memberCompany, String operatorCode) {
        return roamingPriceMapper.getFeeInfo(memberCompany, operatorCode);
    }
}

