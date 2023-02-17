package com.study.board.interceptor;

import com.study.board.domain.JwtPayload;
import com.study.board.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtTokenUtil jwtTokenUtil;

    public AuthInterceptor(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String token = resolveToken(bearerToken);

        JwtPayload jwtPayload = jwtTokenUtil.verifyJWT(token);

        request.setAttribute("user", jwtPayload);

        return true;
    }

    private String resolveToken(String bearerToken) {
        return bearerToken.substring(7);
    }

}
