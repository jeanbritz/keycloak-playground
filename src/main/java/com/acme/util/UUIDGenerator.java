package com.acme.util;

import java.util.UUID;

public class UUIDGenerator implements IdGenerator<String> {
    @Override
    public String next() {
        return UUID.randomUUID().toString();
    }
}
