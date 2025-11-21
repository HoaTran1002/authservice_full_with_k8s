package com.backend.authservice.auth.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final RSAPrivateKey privateKey;

    public JwtService() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
    }

    public String generateToken(String subject) throws JOSEException {
        Instant now = Instant.now();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer("http://localhost:9000")
                .expirationTime(Date.from(now.plusSeconds(3600)))
                .issueTime(Date.from(now))
                .build();

        SignedJWT signedJWT = new SignedJWT(new com.nimbusds.jose.JWSHeader(JWSAlgorithm.RS256), claimsSet);
        signedJWT.sign(new RSASSASigner(privateKey));

        return signedJWT.serialize();
    }
}