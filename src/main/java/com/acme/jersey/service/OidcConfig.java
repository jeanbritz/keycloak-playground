package com.acme.jersey.service;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface OidcConfig {

    void refresh();

    OIDCProviderMetadata metadata();

    JWKSet jwkSet();
}
