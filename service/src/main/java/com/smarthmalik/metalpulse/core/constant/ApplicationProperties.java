package com.smarthmalik.metalpulse.core.constant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class ApplicationProperties {

    private Jwt jwt = new Jwt();
    private Goldbroker goldbroker = new Goldbroker();

    @Getter
    @Setter
    public static class Jwt {
        private String secret = "metalpulse-secret-key-minimum-32-characters-long!!";
        private long expirationMs = 86_400_000L;
    }

    @Getter
    @Setter
    public static class Goldbroker {
        private String baseUrl = "https://goldbroker.com/api";
    }
}
