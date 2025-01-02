package com.acme.jersey.service;

import com.acme.Config;
import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.RefreshTokenGrant;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.ClientSecretPost;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.LogoutRequest;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.UriBuilder;
import org.jvnet.hk2.annotations.Service;

import java.net.URI;

@Service
public class OidcRequestFactoryImpl implements OidcRequestFactory {

    private final OidcConfig oidcConfig;

    @Inject
    public OidcRequestFactoryImpl(OidcConfig oidcConfig) {
        this.oidcConfig = oidcConfig;
    }

    @Override
    public TokenRequest newTokenRequest(String code) {
        URI tokenEndpoint = this.oidcConfig.metadata().getTokenEndpointURI();
        ClientID clientID = new ClientID(Config.getProperty(Config.Key.OIDC_CLIENT_ID));
        Secret clientSecret = new Secret(Config.getProperty(Config.Key.OIDC_CLIENT_SECRET));
        URI recdirectUri = UriBuilder.fromUri(Config.getProperty(Config.Key.OIDC_AUTH_CALLBACK_URL)).build();
        ClientAuthentication clientAuth = new ClientSecretPost(clientID, clientSecret);
        AuthorizationCode authCode = new AuthorizationCode(code);
        AuthorizationGrant authGrant = new AuthorizationCodeGrant(authCode, recdirectUri);
        Scope scope = new Scope("openid");
        return new TokenRequest(tokenEndpoint, clientAuth, authGrant, scope);
    }

    @Override
    public TokenRequest newRefreshTokenRequest(RefreshToken refreshToken) {
        ClientID clientID = new ClientID(Config.getProperty(Config.Key.OIDC_CLIENT_ID));
        Secret clientSecret = new Secret(Config.getProperty(Config.Key.OIDC_CLIENT_SECRET));
        ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret);
        RefreshTokenGrant refreshTokenGrant = new RefreshTokenGrant(refreshToken);
        return new TokenRequest.Builder(this.oidcConfig.metadata().getTokenEndpointURI(), clientAuth, refreshTokenGrant).build();
    }

    @Override
    public LogoutRequest newLogoutRequest(JWT idToken) {
        return new LogoutRequest(this.oidcConfig.metadata().getEndSessionEndpointURI(), idToken);
    }

    @Override
    public URI newAuthorizeRequest(String state, String nonce) {
        return UriBuilder.fromUri(this.oidcConfig.metadata().getAuthorizationEndpointURI())
                .queryParam("client_id", Config.getProperty(Config.Key.OIDC_CLIENT_ID))
                .queryParam("redirect_uri", Config.getProperty(Config.Key.OIDC_AUTH_CALLBACK_URL))
                .queryParam("response_type", "code")
                .queryParam("scope", "openid")
                .queryParam("state", state)
                .queryParam("nonce", nonce)
                .build();
    }

    @Override
    public UserInfoRequest newUserInfoRequest(AccessToken accessToken) {
        return new UserInfoRequest(oidcConfig.metadata().getUserInfoEndpointURI(), accessToken);
    }
}
