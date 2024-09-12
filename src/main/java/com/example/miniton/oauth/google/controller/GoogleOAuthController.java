package com.example.miniton.oauth.google.controller;

import com.example.miniton.common.CommonResponseDto;
import com.example.miniton.oauth.google.service.GoogleOAuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class GoogleOAuthController {
    private final GoogleOAuthService googleOAuthService;

    //OpenAPI 문서 생성 도구에서 해당 메서드를 숨기는 역할을 한다.
    @Operation(summary = "OAuth2 로그인/회원가입 요청", description = "google oauth 로그인 요청")
    @GetMapping(value = "/login-page/google")
    public ResponseEntity<Void> getGoogleAuthUrl() throws Exception {
        //HttpStatus.MOVED_PERMANENTLY 해야 리턴 된 uri 경로로 바로 이동 할 수 있다.
        return new ResponseEntity<>(googleOAuthService.makeLoginURL(), HttpStatus.MOVED_PERMANENTLY);
    }

    @Operation(hidden = true)
    @GetMapping(value = "/login/google")
    public ResponseEntity<CommonResponseDto> sign(@RequestParam(value = "code") String authCode) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto("성공", googleOAuthService.socialLogin(authCode)));
    }

}
