package com.backend.authservice.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;

import java.util.UUID;

@Configuration
public class RegisteredClientSeeder {

    @Bean
    public CommandLineRunner seedRegisteredClient(JdbcTemplate jdbcTemplate) {
        return args -> {
            RegisteredClientRepository repo = new JdbcRegisteredClientRepository(jdbcTemplate);
            // check if client exists by id (jdbc repo requires reading by clientId; try to load via SQL)
            try {
                var rc = jdbcTemplate.queryForObject("select client_id from oauth2_registered_client limit 1", (rs, rowNum) -> rs.getString(1));
                // already has at least one client
                return;
            } catch (Exception ex) {
                // no clients, create one
            }

            PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId("kocham-web-client")
                    .clientSecret(encoder.encode("kocham-secret"))
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .redirectUri("http://localhost:3000/login/oauth2/code/kocham-web-client")
                    .scope("openid")
                    .scope("profile")
                    .scope("email")
                    .tokenSettings(tokenSettings -> tokenSettings.reuseRefreshTokens(false))
                    .clientSettings(clientSettings -> clientSettings.requireAuthorizationConsent(false))
                    .build();
            ((JdbcRegisteredClientRepository) repo).save(client);
        };
    }
}
