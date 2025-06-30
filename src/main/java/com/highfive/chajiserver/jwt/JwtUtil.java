package com.highfive.chajiserver.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "MySuperSecretKeyForJWTTokenWhichIsVerySecure12345";
    private final long EXPIRATION = 1000 * 60 * 60;
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(String userId, int userIdx) {
        return Jwts.builder().setSubject(userId)
                .claim("idx", userIdx)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //토큰에서 userId 추출
    public String getUserIdFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    //토큰에서 userIdx 추출
    public Integer getUserIdxFromToken(String token) {
        return parseClaims(token).get("idx", Integer.class);
    }

    //토큰 유효성 검사
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        }catch(JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    //내부 claims 파싱 메서드
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //요청 헤더에서 토큰 추출 → userIdx 반환
    public Integer getUserIdxFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if(bearer != null && bearer.startsWith("Bearer ")) {
            String token = bearer.substring(7);
            return getUserIdxFromToken(token);
        }
        throw new RuntimeException("토큰 에러!");
    }


}

