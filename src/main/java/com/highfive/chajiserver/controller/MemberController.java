//MemberController
package com.highfive.chajiserver.controller;

import com.highfive.chajiserver.dto.MemberDTO;
import com.highfive.chajiserver.jwt.JwtUtil;
import com.highfive.chajiserver.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService service;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody MemberDTO member) {
        service.register(member);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberDTO loginRequest) {
        String token = service.login(loginRequest.getUserId(), loginRequest.getPassword()); //아이디비번을 빼서 로그인 메서드에 넣는다.
        if (token != null) {
            return ResponseEntity.ok().body(token);
        }else {
            return ResponseEntity.status(401).body("로그인 실패");
        }
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        MemberDTO member = service.getUserInfoFromToken(token);
        if (member != null) {
            return ResponseEntity.ok(member);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        service.logout(token);
        return ResponseEntity.ok("로그아웃 성공(클라이언트에서 토큰 삭제)");
    }
}

