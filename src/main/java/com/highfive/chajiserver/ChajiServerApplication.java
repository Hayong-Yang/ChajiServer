package com.highfive.chajiserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.highfive.chajiserver.mapper")
public class ChajiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChajiServerApplication.class, args);
    }

}
