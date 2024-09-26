package com.example.miniton.oauth.strategy;


import com.example.miniton.oauth.dto.GoogleResponse;
import com.example.miniton.oauth.dto.OAuth2Response;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GoogleOAuth2ResponseStrategy implements OAuth2ResponseStrategy{
    @Override
    public String getProviderName() {
        return "google";
    }

    @Override
    public OAuth2Response createOAuth2Response(Map<String, Object> attributes) {
        System.out.println(attributes);
        return new GoogleResponse(attributes);
    }
}
