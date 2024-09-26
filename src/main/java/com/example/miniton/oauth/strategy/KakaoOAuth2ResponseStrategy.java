package com.example.miniton.oauth.strategy;


import com.example.miniton.oauth.dto.KakaoResponse;
import com.example.miniton.oauth.dto.OAuth2Response;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KakaoOAuth2ResponseStrategy implements OAuth2ResponseStrategy{
    @Override
    public String getProviderName() {
        return "kakao";
    }

    @Override
    public OAuth2Response createOAuth2Response(Map<String, Object> attributes) {
        System.out.println(attributes);
        return new KakaoResponse(attributes);
    }
}
