package com.acme.hk2.service;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.LogoutRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import org.jvnet.hk2.annotations.Contract;

import java.net.URI;

@Contract
public interface OidcRequestFactory {

    TokenRequest newTokenRequest(String code);

    TokenRequest newRefreshTokenRequest(RefreshToken refreshToken);

    LogoutRequest newLogoutRequest(JWT idToken);

    URI newAuthorizeRequest(State state, Nonce nonce);

    UserInfoRequest newUserInfoRequest(AccessToken accessToken);

    State nextState();

    Nonce nextNonce();

}
