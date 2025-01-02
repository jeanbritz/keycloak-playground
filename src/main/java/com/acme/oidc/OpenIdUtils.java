package com.acme.oidc;

import java.security.SecureRandom;
import java.util.Base64;

public class OpenIdUtils {

    private static final SecureRandom secureRandom = new SecureRandom(); // Thread-safe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    /**
     * Generates a secure random string suitable for use as `state` or `nonce`.
     *
     * @return A URL-safe random string
     */
    public static String generateRandomString() {
        byte[] randomBytes = new byte[32]; // 256-bit random value
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
