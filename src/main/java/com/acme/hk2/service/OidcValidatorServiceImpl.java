package com.acme.hk2.service;

import com.acme.Config;
import com.acme.hk2.service.exception.InvalidTokenException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import jakarta.inject.Inject;
import org.jvnet.hk2.annotations.Service;

import java.text.ParseException;

@Service
public class OidcValidatorServiceImpl implements OidcValidatorService {

    private final OidcConfig oidcConfig;

    @Inject
    public OidcValidatorServiceImpl(OidcConfig oidcConfig) {
        this.oidcConfig = oidcConfig;
    }

    @Override
    public boolean isValidIssuer(String issuer) {
        Issuer actualIssuer = new Issuer(issuer);
        return oidcConfig.metadata().getIssuer().equals(actualIssuer);
    }

    @Override
    public void validateIdToken(String idToken, String nonce) {
        ClientID clientID = new ClientID(Config.getProperty(Config.Key.OIDC_CLIENT_ID));
        IDTokenValidator validator = new IDTokenValidator(oidcConfig.metadata().getIssuer(), clientID, JWSAlgorithm.ES256, oidcConfig.jwkSet());
        try {
            validator.validate(JWTParser.parse(idToken), new Nonce(nonce));
        } catch (BadJOSEException | JOSEException | ParseException e) {
            throw new InvalidTokenException("ID token validation failed", e);
        }
    }
}
