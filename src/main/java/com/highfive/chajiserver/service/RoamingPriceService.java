package com.highfive.chajiserver.service;

import com.highfive.chajiserver.dto.RoamingPriceDTO;

public interface RoamingPriceService {
    RoamingPriceDTO getFeeInfo(String memberCompany, String operatorCode);
}
