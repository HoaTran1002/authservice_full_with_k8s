package com.backend.authservice.config;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
import java.time.Duration;
import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private com.nimbusds.jose.jwk.RSAKey rsaJwk;

    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http
            .getConfigurer(OAuth2AuthorizationServerConfigurer.class)
            .oidc(Customizer.withDefaults()); // Enable OpenID Connect 1.0

        // Customize endpoints, CORS, etc. leave for global SecurityConfig
        return http.formLogin(Customizer.withDefaults()).build();
    }

    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder()
                .issuer("http://localhost:9000")
                .build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        // Use JdbcRegisteredClientRepository backed by DataSource.
        JdbcRegisteredClientRepository repo = new JdbcRegisteredClientRepository(jdbcTemplate);
        return repo;
    }

    @Bean
    public JdbcOAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        // Convert RSAKey (from JwtConfig) to Nimbus JWKSource
        com.nimbusds.jose.jwk.RSAKey rsa = rsaJwk;
        com.nimbusds.jose.jwk.JWKSet jwkSet = new com.nimbusds.jose.jwk.JWKSet(rsa);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder() throws Exception {
        return org.springframework.security.oauth2.jwt.NimbusJwtDecoder.withPublicKey(rsaJwk.toRSAPublicKey()).build();
    }
}
