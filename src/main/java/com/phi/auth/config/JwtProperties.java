package com.phi.auth.config;

import java.nio.file.Path;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.security.jwt")
public record JwtProperties(
        Path jwtKey,
        String keyId,
        String issuer,
        String audience,
        Duration expiration,
        Duration refreshInterval) {

}