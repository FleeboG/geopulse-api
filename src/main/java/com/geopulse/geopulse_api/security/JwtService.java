package com.geopulse.geopulse_api.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JwtService {

    private static final Base64.Encoder B64_URL = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder B64_URL_DEC = Base64.getUrlDecoder();

    // Defaults keep your app/test context from failing if you haven't set env vars yet.
    @Value("${security.jwt.secret:dev-secret-change-me-dev-secret-change-me}")
    private String secret;

    @Value("${security.jwt.expiration-seconds:3600}")
    private long expirationSeconds;

    public String generateToken(String subject) {
        long now = Instant.now().getEpochSecond();
        long exp = now + expirationSeconds;

        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payloadJson = "{\"sub\":\"" + escape(subject) + "\",\"iat\":" + now + ",\"exp\":" + exp + "}";

        String header = B64_URL.encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
        String payload = B64_URL.encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));

        String unsigned = header + "." + payload;
        String sig = sign(unsigned);

        return unsigned + "." + sig;
    }

    public boolean isValid(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String unsigned = parts[0] + "." + parts[1];
            String expectedSig = sign(unsigned);

            // constant-time compare
            if (!constantTimeEquals(expectedSig, parts[2])) return false;

            String payloadJson = new String(B64_URL_DEC.decode(parts[1]), StandardCharsets.UTF_8);
            long exp = extractLong(payloadJson, "exp");
            long now = Instant.now().getEpochSecond();

            return now < exp;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractSubject(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) throw new IllegalArgumentException("Invalid token");

        String payloadJson = new String(B64_URL_DEC.decode(parts[1]), StandardCharsets.UTF_8);
        return extractString(payloadJson, "sub");
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(key);
            byte[] sig = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return B64_URL.encodeToString(sig);
        } catch (Exception e) {
            throw new IllegalStateException("JWT signing failed", e);
        }
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String extractString(String json, String key) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher m = p.matcher(json);
        if (!m.find()) throw new IllegalArgumentException("Missing claim: " + key);
        return m.group(1);
    }

    private static long extractLong(String json, String key) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(\\d+)");
        Matcher m = p.matcher(json);
        if (!m.find()) throw new IllegalArgumentException("Missing claim: " + key);
        return Long.parseLong(m.group(1));
    }
}