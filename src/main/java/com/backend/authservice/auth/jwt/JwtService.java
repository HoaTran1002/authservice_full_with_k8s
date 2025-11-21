package com.backend.authservice.auth.jwt;

import com.backend.authservice.auth.token.TokenEntity;
import com.backend.authservice.auth.token.TokenRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final com.nimbusds.jose.jwk.RSAKey rsaJwk;
    private final TokenRepository tokenRepository;

    @Value("${app.jwt.access-token-ttl-seconds:900}")
    private long accessTokenTtl;

    @Value("${app.jwt.refresh-token-ttl-seconds:604800}")
    private long refreshTokenTtl;

    public String issueAccessToken(String userId, String email, String roles) throws JOSEException {
        RSAPrivateKey privateKey = rsaJwk.toRSAPrivateKey();

        JWSSigner signer = new RSASSASigner(privateKey);

        Instant now = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(userId)
                .claim("email", email)
                .claim("roles", roles)
                .issuer("http://localhost:9000")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(accessTokenTtl)))
                .jwtID(UUID.randomUUID().toString())
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(rsaJwk.getKeyID())
                .type(JOSEObjectType.JWT)
                .build();

        SignedJWT signedJWT = new SignedJWT(header, claims);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    public TokenEntity createRefreshToken(String userId) {
        TokenEntity t = new TokenEntity();
        t.setId(UUID.randomUUID().toString());
        t.setUserId(userId);
        t.setExpiresAt(Instant.now().plusSeconds(refreshTokenTtl));
        tokenRepository.save(t);
        return t;
    }

    public TokenEntity rotateRefreshToken(String oldTokenId) {
        TokenEntity old = tokenRepository.findById(oldTokenId).orElseThrow();
        tokenRepository.delete(old);
        return createRefreshToken(old.getUserId());
    }
}
