package com.highfive.chajiserver.service;

import com.highfive.chajiserver.mapper.UserCarConnectorMapper;
import org.springframework.stereotype.Service;

@Service
public class UserCarConnectorService {

    private final UserCarConnectorMapper userCarConnectorMapper;

    public UserCarConnectorService(UserCarConnectorMapper userCarConnectorMapper) {
        this.userCarConnectorMapper = userCarConnectorMapper;
    }

    public void createFromCarSpec(int userCarIdx, int carIdx) {
        userCarConnectorMapper.insertFromCarSpec(userCarIdx, carIdx);
    }
}
