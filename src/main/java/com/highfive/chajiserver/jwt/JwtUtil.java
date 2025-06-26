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

    public String generateToken(String username, int userId) {
        return Jwts.builder().setSubject(username)
                .claim("id", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //토큰에서 userName 추출
    public String getUserNameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    //토큰에서 userId 추출
    public Integer getUserIdFromToken(String token) {
        return parseClaims(token).get("id", Integer.class);
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


}

