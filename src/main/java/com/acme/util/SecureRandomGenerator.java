package com.acme.util;

import java.security.SecureRandom;
import java.util.Base64;

public class SecureRandomGenerator implements IdGenerator<String> {

    private static final SecureRandom secureRandom = new SecureRandom(); // Thread-safe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    @Override
    public String next() {
        byte[] randomBytes = new byte[32]; // 256-bit random value
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
