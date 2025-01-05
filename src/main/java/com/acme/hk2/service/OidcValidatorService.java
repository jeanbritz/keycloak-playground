package com.acme.hk2.service;

import com.nimbusds.openid.connect.sdk.Nonce;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface OidcValidatorService {

    /**
     * Security: Ensures the issuer came from the expected Identity Provider (IDP) and not from some malicious or unintended source.
     * @param issuer
     * @return
     */
    boolean isValidIssuer(String issuer);

    /**
     *
     * @param initialState
     * @param nextState
     * @return
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2">RFC 6749: Authorization Response</a>
     */
    boolean isValidState(String initialState, String nextState);

    /**
     * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#IDTokenValidation">OpenID Connect 1.0: ID Token Validation</a>
     */
    void validateIdToken(String idToken, String nonce);
}
