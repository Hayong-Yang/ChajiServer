package com.highfive.chajiserver.service;

import com.highfive.chajiserver.dto.ChargerFeeDTO;
import com.highfive.chajiserver.mapper.ChargerFeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChargerFeeService {
    private final ChargerFeeMapper chargerFeeMapper;

    public ChargerFeeDTO getFeeByBusiId(String busiId) {
        return chargerFeeMapper.findByBusiId(busiId);
    }
}


