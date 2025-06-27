package com.highfive.chajiserver.controller;

import com.highfive.chajiserver.service.UserCarConnectorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/carconnector")
public class UserCarConnectorController {

    private final UserCarConnectorService userCarConnectorService;

    public UserCarConnectorController(UserCarConnectorService userCarConnectorService) {
        this.userCarConnectorService = userCarConnectorService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUserCarConnector(@RequestParam int userCarIdx,
                                                           @RequestParam int carIdx) {
        userCarConnectorService.createFromCarSpec(userCarIdx, carIdx);
        return ResponseEntity.ok("차량 커넥터 정보 복사 완료");
    }
}
