package com.smarthmalik.metalpulse.configuration;

import com.smarthmalik.metalpulse.core.constant.ApplicationProperties;
import com.smarthmalik.metalpulse.core.constant.ResourceConstants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
@RequiredArgsConstructor
public class AppConfiguration {

    private final ApplicationProperties appProperties;

    @Bean(ResourceConstants.GOLDBROKER_REST_CLIENT)
    public RestClient goldbrokerRestClient() {
        return RestClient.builder()
                .baseUrl(appProperties.getGoldbroker().getBaseUrl())
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MetalPulse — Precious Metals Price API")
                        .version("1.0.0")
                        .description("Live price feeds, historical data, analytics, and trading for gold, silver, platinum and palladium")
                        .contact(new Contact().name("smarthmalik").email("tattaisreeram@gmail.com")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste the JWT token from /api/v1/auth/login here")));
    }
}
