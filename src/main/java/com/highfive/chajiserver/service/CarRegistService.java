package com.highfive.chajiserver.service;

import com.highfive.chajiserver.dto.CarRegistDTO;
import com.highfive.chajiserver.mapper.UserCarMapper;
import com.highfive.chajiserver.mapper.UserCarConnectorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarRegistService {

    private final UserCarMapper userCarMapper;
    private final UserCarConnectorMapper userCarConnectorMapper;

    public void registerCar(CarRegistDTO req) {
        int carCount = userCarMapper.countByMemberIdx(req.getMemberIdx());
        if (carCount >= 2) {
            throw new IllegalStateException("최대 2대까지만 등록 가능합니다.");
        }

        userCarMapper.insertUserCar(req);
        int userCarIdx = req.getUserCarIdx(); // insert 시 useGeneratedKeys 로 주입됨

        userCarConnectorMapper.insertFromCarSpec(userCarIdx, req.getCarIdx());
    }
}
