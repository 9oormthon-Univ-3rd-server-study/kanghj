package com.example.miniton.oauth.strategy;


import com.example.miniton.oauth.dto.NaverResponse;
import com.example.miniton.oauth.dto.OAuth2Response;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NaverOAuth2ResponseStrategy implements OAuth2ResponseStrategy{
    @Override
    public String getProviderName() {
        return "naver";
    }

    @Override
    public OAuth2Response createOAuth2Response(Map<String, Object> attributes) {
        return new NaverResponse(attributes);
    }
}
