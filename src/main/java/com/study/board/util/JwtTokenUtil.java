package com.study.board.util;

import com.study.board.domain.JwtPayload;
import com.study.board.domain.Role;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

    private final String secret;
    private final long tokenValidityInMilliseconds;

    public JwtTokenUtil(@Value("${jwt.secret}") String secret,
                        @Value("${jwt.token-validity-in-seconds}") long tokenValidityInMilliseconds) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds * 1000;
    }

    // 토큰 생성
    public String createToken(JwtPayload jwtPayload) {

        // 헤더
        Map<String, Object> headers = new HashMap<>();

        // Type 설정
        headers.put("typ", "JWT");
        // 암호화 정보
        headers.put("alg", "HS512");

        // payload
        Date ext = new Date();
        ext.setTime(ext.getTime() + tokenValidityInMilliseconds);

        Map<String, Object> claims = new HashMap<>();

        claims.put("id", jwtPayload.getId().toString());
        claims.put("loginId", jwtPayload.getLoginId());
        claims.put("role", jwtPayload.getRole().toString());

        // 토큰 생성
        String jwt = Jwts.builder()
                .setHeader(headers)     // Headers 설정
                .addClaims(claims)
                .setSubject("user-auth") // 토큰 용도
                .setExpiration(ext)     // 토큰 만료 시간
                .signWith(getKey()) // HS512와 key로 sign
                .compact(); // 토큰 생성

        return jwt;
    }

    // 토큰 검증
    public JwtPayload verifyJWT(String token) throws IOException {
        JwtPayload jwtPayload = null;
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Long id = Long.valueOf((String) claims.get("id"));
            String loginId = (String) claims.get("loginId");
            String role = (String) claims.get("role");

            jwtPayload = new JwtPayload(id, loginId, Role.valueOf(role));

        } catch (ExpiredJwtException e) { // 토큰이 만료되었을 경우
            System.out.println("expire === ");
            System.out.println(e);
            throw e;
        } catch (Exception e) { // 그 외 에러
            System.out.println("error === ");
            System.out.println(e);
            throw e;
        }

        return jwtPayload;
    }

    private Key getKey() {
        byte[] secretBytes = secret.getBytes();
        return new SecretKeySpec(secretBytes, SignatureAlgorithm.HS512.getJcaName());
    }
}
