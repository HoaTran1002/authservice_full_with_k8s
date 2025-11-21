package com.backend.authservice.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import javax.sql.DataSource;
import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {

    @Bean
    public RegisteredClientRepository registeredClientRepository(DataSource dataSource) {
        JdbcRegisteredClientRepository repository = new JdbcRegisteredClientRepository(dataSource);

        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("admin-client")
                .clientSecret("{noop}admin-secret")
                .scope("openid")
                .scope("profile")
                .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();

        repository.save(client);
        return repository;
    }

    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder()
                .issuer("http://localhost:9000")
                .build();
    }

    @Bean
    public org.springframework.security.web.SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
            .formLogin();
        return http.build();
    }
}