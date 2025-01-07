package com.acme.hk2.service;

import com.acme.Config;
import com.acme.jakarta.resource.exception.OidcMetadataException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;


@Service
public class OidcProviderConfigImpl implements OidcProviderConfig {

    private static final Logger logger = LoggerFactory.getLogger(OidcProviderConfigImpl.class);
    private final long CACHE_TTL_SECONDS = 3600; // Cache Time-to-Live (1 hour)
    private OIDCProviderMetadata cachedMetadata;
    private JWKSet jwkSet;
    private Instant cacheExpiryTime;
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public void refresh() {

        String issuerURL = Config.getProperty(Config.Key.OIDC_ISSUER_URL);
        Issuer issuer = new Issuer(issuerURL);
        try {
            lock.lock();
            // https://datatracker.ietf.org/doc/html/rfc8414#section-3.1
            cachedMetadata = OIDCProviderMetadata.resolve(issuer);
            jwkSet = JWKSet.load(cachedMetadata.getJWKSetURI().toURL());
        } catch (GeneralException | IOException | ParseException e) {
            throw new OidcMetadataException("failed to refresh OIDC metadata", e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public OIDCProviderMetadata metadata() {
        if (cachedMetadata == null || Instant.now().isAfter(cacheExpiryTime)) {
            logger.debug("Refreshing OIDC metadata...");
            refresh();
            cacheExpiryTime = Instant.now().plusSeconds(CACHE_TTL_SECONDS);
        } else {
            logger.debug("Using cached OIDC metadata...");
        }
        return cachedMetadata;
    }

    @Override
    public JWKSet jwkSet() {
        if(cachedMetadata == null) {
            refresh();
        }
        return jwkSet;
    }
}
