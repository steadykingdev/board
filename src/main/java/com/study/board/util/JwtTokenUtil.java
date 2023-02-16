package com.study.board.util;

import com.study.board.domain.JwtPayload;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
        Map<String, Object> headers = new HashMap<>();

        // Type 설정
        headers.put("typ", "JWT");
        // 암호화 정보
        headers.put("alg", "HS512");

        Date ext = new Date();
        ext.setTime(ext.getTime() + tokenValidityInMilliseconds);

        // 토큰 생성
        String jwt = Jwts.builder()
                .setHeader(headers)
                .claim("user",jwtPayload)
                .setSubject("user-auth")
                .setExpiration(ext)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        return jwt;
    }

    // 토큰 검증
    public Map<String, Object> verifyJWT(String authorization) throws UnsupportedEncodingException {
        Map<String, Object> claimMap = null;

        try {
            claimMap = Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(authorization) // 파싱 및 검증, 실패 시 에러
                    .getBody();
        } catch (ExpiredJwtException e) { // 토큰이 만료되었을 경우
            System.out.println("expire === ");
            System.out.println(e);
        } catch (Exception e) { // 그 외 에러
            System.out.println("error === ");
            System.out.println(e);
        }
        return claimMap;
    }
}
