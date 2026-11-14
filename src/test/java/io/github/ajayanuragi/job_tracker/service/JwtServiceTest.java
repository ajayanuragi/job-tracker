package io.github.ajayanuragi.job_tracker.service;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;
import io.github.ajayanuragi.job_tracker.config.JwtProperties;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;

class JwtServiceTest {

    @Test
    void generateToken() {
        byte[] keyBytes = new byte[32];
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");

        JwtEncoder encoder = new NimbusJwtEncoder(
                new ImmutableSecret<>(secretKey)
        );

        JwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        JwtProperties properties = new JwtProperties();
        properties.setExpiration(3600000);

        JwtService jwtService = new JwtService(encoder, properties);

        String token = jwtService.generateToken("test@example.com");
        Jwt jwt = decoder.decode(token);

        assertEquals("test@example.com", jwt.getSubject());
        assertNotNull(jwt.getIssuedAt());
        assertNotNull(jwt.getExpiresAt());
    }

    @Test
    void decodeExpiredToken_throwsException(){
        byte[] keyBytes = new byte[32];
        SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");

        JwtEncoder encoder = new NimbusJwtEncoder(
                new ImmutableSecret<>(secretKey)
        );

        JwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject("test@example.com")
                .issuedAt(now.minusSeconds(7200))
                .expiresAt(now.minusSeconds(3600))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        String expiredToken = encoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();

        assertThrows(JwtException.class, () -> decoder.decode(expiredToken));
    }

    @Test
    void decodeTokenWithWrongSecret_throwsException(){
        byte[] signingKeyBytes = new byte[32];
        SecretKey signingKey = new SecretKeySpec(signingKeyBytes, "HmacSHA256");

        byte[] verificationKeyBytes = new byte[32];
        verificationKeyBytes[0] = 1;

        SecretKey verificationKey = new SecretKeySpec(
                verificationKeyBytes, "HmacSHA256"
        );

        JwtEncoder encoder = new NimbusJwtEncoder(
                new ImmutableSecret<>(signingKey)
        );

        JwtDecoder decoder = NimbusJwtDecoder.withSecretKey(verificationKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject("test@example.com")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        String token = encoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();

        assertThrows(JwtException.class, () -> decoder.decode(token));

    }
}