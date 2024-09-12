package com.example.miniton.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.models.info.Info;


@OpenAPIDefinition(servers = {@Server(url = "/", description = "default generated url")})
@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI springOpenAPI(){
        
        // API 요청헤더에 인증 정보 포함
        SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList("JWT");

        //Security 스키마 설정
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION);

        //OpenAPI 객체 생성 후 반환
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("JWT",bearerAuth))
                .addSecurityItem(securityRequirement)
                .info(info());
    }

    private Info info(){
        return new Info()
                .title("MINITON API Document")
                .version("0.1")
                .description("MINITON 프로젝트 API 명세서 입니다.");
    }

}
