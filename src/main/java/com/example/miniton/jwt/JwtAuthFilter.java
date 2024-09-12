package com.example.miniton.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = jwtProvider.getAuthorizationToken(request);
        log.info("[doFilterInternal] Token ={}", token);

        if (token != null) {
            String jwtToken = token.substring(7);
            log.info("[doFilterInternal] jwtToken:{}", jwtToken);

            log.info("[doFilterInternal] 토큰 타입 확인");
            if (request.getRequestURI().equals("/v1/oauth2/reissue")) {
                jwtProvider.validRefreshToken(jwtToken);
            } else {
                jwtProvider.validAccessToken(jwtToken);
            }

            jwtProvider.validateToken(jwtToken);
            Authentication authentication = jwtProvider.getAuthentication(jwtToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("[doFilterInternal] 토큰 값 검증 완료.git");
        }

        filterChain.doFilter(request, response);
    }
}