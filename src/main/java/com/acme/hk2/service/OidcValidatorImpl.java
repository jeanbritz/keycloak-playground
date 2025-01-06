package com.acme.hk2.service;

import com.acme.Config;
import com.acme.jakarta.resource.exception.InvalidTokenException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import jakarta.inject.Inject;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

import static com.acme.log.Markers.SECURITY_MARKER;

@Service
public class OidcValidatorImpl implements OidcValidator {

    private static final Logger logger = LoggerFactory.getLogger(OidcValidatorImpl.class);

    private final OidcConfig oidcConfig;

    @Inject
    public OidcValidatorImpl(OidcConfig oidcConfig) {
        this.oidcConfig = oidcConfig;
    }

    @Override
    public boolean isValidIssuer(String issuer) {
        Issuer expected = oidcConfig.metadata().getIssuer();
        Issuer actual = new Issuer(issuer);
        boolean result = expected.equals(actual);
        if (!result) {
            logger.warn(SECURITY_MARKER, "Incoming request contains different issuer than expected [expected: {}, actual: {}]",
                    expected, actual);
        }
        return result;
    }

    @Override
    public boolean isValidState(String initialState, String nextState) {
        State initial = new State(initialState);
        State next = new State(nextState);
        boolean result = initial.equals(next);
        if(!result) {
            logger.warn(SECURITY_MARKER, "Incoming request contains mismatch between state values, possible CSRF attack [expected: {}, actual: {}]",
                    initial, next);
        }
        return result;
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
