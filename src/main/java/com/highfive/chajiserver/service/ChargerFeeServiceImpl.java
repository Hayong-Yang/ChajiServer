package com.highfive.chajiserver.service;

import com.highfive.chajiserver.dto.ChargerFeeDTO;
import com.highfive.chajiserver.mapper.ChargerFeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChargerFeeServiceImpl implements ChargerFeeService {

    private final ChargerFeeMapper chargerFeeMapper;

    @Override
    public List<ChargerFeeDTO> getAllFees() {
        return chargerFeeMapper.getAllFees();
    }
}
