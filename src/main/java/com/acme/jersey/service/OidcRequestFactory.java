package com.acme.jersey.service;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.LogoutRequest;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import org.jvnet.hk2.annotations.Contract;

import java.net.URI;

@Contract
public interface OidcRequestFactory {

    TokenRequest newTokenRequest(String code);

    TokenRequest newRefreshTokenRequest(RefreshToken refreshToken);

    LogoutRequest newLogoutRequest(JWT idToken);

    URI newAuthorizeRequest(String state, String nonce);

    UserInfoRequest newUserInfoRequest(AccessToken accessToken);


}
