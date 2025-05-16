package com.bookreum.dev.domain.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import javax.crypto.SecretKey;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class JwtConfig {

    @Bean
    public SecretKey jwtSigningKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    @PostConstruct
    public void logJwtKey() {
        String base64Key = Base64.getEncoder().encodeToString(jwtSigningKey().getEncoded());
        log.info("Generated JWT key (Base64): {}", base64Key);
    }
}
