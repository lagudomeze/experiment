package com.phi.auth.service;

import com.phi.auth.config.JwtProperties;
import com.phi.common.BizError;
import com.phi.common.BizException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.spec.InvalidKeySpecException;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.EcJwkGenerator;
import org.jose4j.jwk.EllipticCurveJsonWebKey;
import org.jose4j.jwk.JsonWebKey.Factory;
import org.jose4j.jwk.JsonWebKey.OutputControlLevel;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.EllipticCurves;
import org.jose4j.lang.JoseException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private record JwtSerde(JsonWebSignature jws, JwtConsumer consumer) {

    }

    private final ThreadLocal<JwtSerde> holder;

    private final PublicJsonWebKey key;

    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.holder = ThreadLocal.withInitial(this::newSerde);
        this.properties = properties;
        try {
            key = init(properties);
        } catch (IOException | InvalidKeySpecException | JoseException e) {
            throw new RuntimeException(e);
        }
    }

    private static PublicJsonWebKey init(JwtProperties properties)
            throws IOException, JoseException, InvalidKeySpecException {
        Path path = properties.jwtKey();
        if (path.toFile().exists()) {
            // recover from file
            return (PublicJsonWebKey) Factory.newJwk(Files.readString(path));
        } else {
            // new key and save to file
            EllipticCurveJsonWebKey curveJsonWebKey = EcJwkGenerator.generateJwk(
                    EllipticCurves.P256);
            Files.writeString(path,
                    curveJsonWebKey.toJson(OutputControlLevel.INCLUDE_PRIVATE),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE);
            curveJsonWebKey.setKeyId(properties.keyId());
            return curveJsonWebKey;
        }
    }

    private JwtSerde newSerde() {
        JsonWebSignature jws = new JsonWebSignature();

        jws.setKey(key.getPrivateKey());
        jws.setKeyIdHeaderValue(key.getKeyId());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256);

        JwtConsumer consumer = new JwtConsumerBuilder()
                // the JWT must have an expiration time
                .setRequireExpirationTime()
                // allow some leeway in validating time based claims to account for clock skew
                .setAllowedClockSkewInSeconds(30)
                // the JWT must have a subject claim
                .setRequireSubject()
                // whom the JWT needs to have been issued by
                .setExpectedIssuer(properties.issuer())
                // to whom the JWT is intended for
                .setExpectedAudience(properties.audience())
                // verify the signature with the public key
                .setVerificationKey(key.getPublicKey())
                // only allow the expected signature algorithm(s) in the given context
                // which is only ECDSA_USING_P256_CURVE_AND_SHA256 here
                .setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT,
                        AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256)
                // create the JwtConsumer instance
                .build();

        return new JwtSerde(jws, consumer);
    }


    public String encode(Authentication authentication) {
        // Create the Claims, which will be the content of the JWT
        JwtClaims claims = new JwtClaims();
        // who creates the token and signs it
        claims.setIssuer(properties.issuer());
        // to whom the token is intended to be sent
        claims.setAudience(properties.audience());
        // time when the token will expire (10 minutes from now)
        claims.setExpirationTimeMinutesInTheFuture(properties.expiration().toMinutes());
        // a unique identifier for the token
        claims.setGeneratedJwtId();
        // when the token was issued/created (now)
        claims.setIssuedAtToNow();
        // time before which the token is not yet valid (2 minutes ago)
        claims.setNotBeforeMinutesInThePast(2);
        // the subject/principal is whom the token is about
        claims.setSubject(authentication.getName());
        claims.setClaim("userId", userId(authentication));
        JwtSerde jwtSerde = holder.get();
        jwtSerde.jws.setPayload(claims.toJson());
        try {
            return jwtSerde.jws.getCompactSerialization();
        } catch (JoseException e) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR,
                    BizError.error("jwt token generate failed"), e);
        }
    }

    private String userId(Authentication authentication) {
        //todo
        return "null";
    }


}