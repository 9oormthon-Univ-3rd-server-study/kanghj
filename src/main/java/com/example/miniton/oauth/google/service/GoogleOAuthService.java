package com.example.miniton.oauth.google.service;

import com.example.miniton.common.domain.Role;
import com.example.miniton.jwt.JwtProvider;
import com.example.miniton.member.domain.Member;
import com.example.miniton.member.repository.MemberRepository;
import com.example.miniton.oauth.google.dto.req.GoogleTokenReqDto;
import com.example.miniton.oauth.google.dto.res.GoogleMemberInfoResDto;
import com.example.miniton.oauth.google.dto.res.GoogleTokenResDto;
import com.example.miniton.redis.RefreshToken;
import com.example.miniton.redis.RefreshTokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class GoogleOAuthService {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.provider.google.authorization-uri}")
    private String googleAuthUrl;

    @Value("${spring.security.oauth2.client.provider.google.token-uri}")
    private String googleTokenUrl;

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String googleUserInfoUrl;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUrl;

    @Value("${spring.security.oauth2.client.registration.google.authorization-grant-type}")
    private String googleAuthGrantType;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    public HttpHeaders makeLoginURL(){
        String reqUrl = googleAuthUrl + "/o/oauth2/v2/auth?client_id=" + googleClientId + "&redirect_uri=" + googleRedirectUrl
                + "&response_type=code&scope=email%20profile%20openid&access_type=offline";

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(reqUrl));

        return headers;
    }

    public Map<String, String> socialLogin(String authCode) throws JsonProcessingException {
        GoogleTokenReqDto googleTokenReqDto = GoogleTokenReqDto.from(googleClientId, googleClientSecret, authCode, googleRedirectUrl, googleAuthGrantType);

        // 구글에서 AccessToken 을 받아온다.
        String googleToken = getGoogleToken(googleTokenReqDto);

        // 해당 AccessToken을 통해 사용자의 정보를 불러온다.
        GoogleMemberInfoResDto googleUserInfoResDto = getGoogleInfo(googleToken);
        
        // googleInfo dto이용, user 존재하면 login, 없으면 회원가입
        Member googleMember = checkExistMember(googleUserInfoResDto);

        Map<String, String> tokens = new HashMap<>();

        String accessToken = jwtProvider.createAccessToken(googleMember.getMemberId(), googleMember.getEmail(), googleMember.getRoles());
        String refreshToken = jwtProvider.createRefreshToken(googleMember.getMemberId(), googleMember.getEmail(), googleMember.getRoles());
        refreshTokenRepository.save(new RefreshToken(googleMember.getMemberId(), refreshToken));
        tokens.put("Access", accessToken);
        tokens.put("Refresh", refreshToken);

        return tokens;
    }

    public String getGoogleToken(GoogleTokenReqDto googleTokenReqDto){
        GoogleTokenResDto googleTokenResDto = webClient.post()
                .uri(googleTokenUrl + "/token")
                .bodyValue(googleTokenReqDto)
                .retrieve()              //retrieve()는 HTTP 응답을 수신하고, 그 응답을 처리할 수 있는 체인을 시작
                .bodyToMono(GoogleTokenResDto.class)
                .block();

        return Objects.requireNonNull(googleTokenResDto).id_token();
    }

    public GoogleMemberInfoResDto getGoogleInfo(String googleToken) throws JsonProcessingException{
        // https://oauth2.googleapis.com/tokeninfo?id_token=<googleToken>
        String requestUrl = UriComponentsBuilder.fromHttpUrl(googleUserInfoUrl)
                .queryParam("id_token", googleToken)
                .toUriString();

        return webClient.get()
                .uri(requestUrl)
                .retrieve()               //retrieve()는 HTTP 응답을 수신하고, 그 응답을 처리할 수 있는 체인을 시작
                .bodyToMono(GoogleMemberInfoResDto.class)
                .block();
    }

    public Member checkExistMember(GoogleMemberInfoResDto googleMemberInfoResDto){
        Optional<Member> oldMember = memberRepository.findByEmail(googleMemberInfoResDto.email());
        Member newMember;

        if(oldMember.isEmpty()){
            log.info("[checkExistMember] 첫 로그인, 회원가입 시작");

            List<String> role = new ArrayList<>();
            role.add(Role.ROLE_USER.getRole());

            newMember = Member.builder()
                    .name(googleMemberInfoResDto.given_name())
                    .email(googleMemberInfoResDto.email())
                    .nickName("google")
                    .password("random")
                    .build();

            memberRepository.save(newMember);
            log.info("[checkExistMember] 회원가입 성공");
            return newMember;
        }
        log.info("[checkExistMember] 이미 가입된 유저입니다.");

        return oldMember.get();
    }

    




}
