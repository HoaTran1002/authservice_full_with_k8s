package com.metainnotech.authservice.auth.controller;

import com.metainnotech.authservice.auth.service.RegisteredClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/admin/clients")
public class ClientAdminController {

    private final RegisteredClientRepository clientRepository;
    private final RegisteredClientService registeredClientService;
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public ClientAdminController(RegisteredClientRepository clientRepository, RegisteredClientService registeredClientService) {
        this.clientRepository = clientRepository;
        this.registeredClientService = registeredClientService;
    }

    @PostMapping
    public ResponseEntity<?> registerClient(@RequestBody ClientCreateRequest req) {
        RegisteredClient.Builder builder = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(req.clientId)
                .clientSecret(passwordEncoder.encode(req.clientSecret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);

        if (req.grantTypes != null) {
            for (String g : req.grantTypes) {
                builder.authorizationGrantType(new AuthorizationGrantType(g));
            }
        }
        if (req.redirectUris != null) builder.redirectUris(uris -> uris.addAll(req.redirectUris));
        if (req.scopes != null) builder.scopes(scopes -> scopes.addAll(req.scopes));

        RegisteredClient client = builder.build();
        clientRepository.save(client);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(registeredClientService.listClientIds());
    }

    public static class ClientCreateRequest {
        public String clientId;
        public String clientSecret;
        public java.util.List<String> grantTypes;
        public java.util.List<String> redirectUris;
        public java.util.List<String> scopes;
    }
}
