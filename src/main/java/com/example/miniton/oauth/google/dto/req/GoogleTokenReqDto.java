package com.example.miniton.oauth.google.dto.req;

import lombok.Builder;

@Builder
public record GoogleTokenReqDto(
        String redirectUri,
        String clientId,
        String clientSecret,
        String code,
        String grantType
) {
    public static GoogleTokenReqDto from(String clientId, String clientSecret, String code, String redirectUri, String grantType){
        return GoogleTokenReqDto.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .code(code)
                .redirectUri(redirectUri)
                .grantType(grantType)
                .build();
    }
}
