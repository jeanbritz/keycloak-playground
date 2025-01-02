package com.acme.jersey.service;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface OidcValidatorService {

    boolean isValidIssuer(String issuer);

    // https://openid.net/specs/openid-connect-core-1_0.html#IDTokenValidation
    void validateIdToken(String idToken, String nonce);
}
