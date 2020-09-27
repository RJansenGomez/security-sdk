package org.rjansen.sdk.security;

import org.rjansen.sdk.exceptions.EncodingException;
import org.rjansen.sdk.exceptions.TokenExpirationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Component
public class SecurityEncryptor {
    private final String secretKey;
    private final int defaultMinutes;
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    public SecurityEncryptor(@Value(value = "${secret.jwt}") String secretKey,
                             @Value(value = "${session.minutes}") int defaultMinutes) {
        this.secretKey = secretKey;
        this.defaultMinutes = defaultMinutes;
    }

    public static String encodePassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    public static boolean samePassword(String rawPassword, String password) {
        return new BCryptPasswordEncoder().matches(rawPassword, password);
    }

    public String encodeHash256(String value) throws EncodingException {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            return Base64.encodeBase64String(sha256_HMAC.doFinal(value.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new EncodingException(e.getMessage(), e);
        }
    }

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return Base64.encodeBase64URLSafeString(randomBytes);
    }

    public String createJWT(SecurityUser securityUser, int minutes) {
        Date now = new Date(System.currentTimeMillis());
//        Key signingKey = createSigningKey();
        JwtBuilder builder = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setIssuer(securityUser.getUsername())
                .signWith(SIGNATURE_ALGORITHM, createSigningKey());
        setExpirationTime(minutes, builder);
        return "Bearer " + builder.compact();
    }

    public String createJWT(SecurityUser securityUser) {
        Date now = new Date(System.currentTimeMillis());
//        Key signingKey = createSigningKey();
        JwtBuilder builder = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setIssuer(securityUser.getUsername())
                .signWith(SIGNATURE_ALGORITHM, createSigningKey());
        setExpirationTime(defaultMinutes, builder);
        return "Bearer " + builder.compact();
    }

    private Key createSigningKey() {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        return new SecretKeySpec(apiKeySecretBytes, SIGNATURE_ALGORITHM.getJcaName());
    }

    private void setExpirationTime(int minutes, JwtBuilder builder) {
        if (minutes >= 0) {
            LocalDateTime dateTime = LocalDateTime.now().plusMinutes(minutes);
            builder.setExpiration(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()));
        }
    }

    public SecurityUser getUserFromJWT(String jwt){
        if (!isTokenValid(jwt)) {
            throw new TokenExpirationException();
        } else {
            return extractUser(jwt);
        }
    }

    private SecurityUser extractUser(String jwt) {
        jwt = jwt.replace("Bearer ", "");
        Claims claims = Jwts.parser()
                .setSigningKey(createSigningKey())
                .parseClaimsJws(jwt).getBody();

        if (isTokenExpired(claims.getExpiration())) {
            throw new TokenExpirationException();
        } else {
            return setUser(claims);

        }
    }

    private boolean isTokenExpired(Date expiration) {
        return new Date().compareTo(expiration) != -1;
    }

    private boolean isTokenValid(String jwt) {
        return null != jwt && !jwt.isEmpty() && jwt.startsWith("Bearer ");
    }

    private SecurityUser setUser(Claims claims) {
        return new SecurityUser(claims.getIssuer());
    }

}
