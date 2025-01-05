package com.acme.jakarta.resource.oidc;

import com.acme.Config;
import com.acme.hk2.service.OidcConfig;
import com.acme.hk2.service.OidcRequestFactory;
import com.acme.hk2.service.OidcValidatorService;
import com.acme.hk2.service.WebSessionHelperService;
import com.acme.hk2.service.exception.OidcCallbackException;
import com.acme.hk2.service.exception.OidcLogoutException;
import com.acme.hk2.service.exception.OidcUserInfoException;
import com.acme.jakarta.security.OAuthSecurityContext;
import com.acme.oidc.SessionAttrs;
import com.acme.oidc.TokensDto;
import com.acme.oidc.UserInfoDto;
import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.token.Tokens;
import com.nimbusds.openid.connect.sdk.LogoutRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;

@Path("/")
@Hidden
public class OidcResource {

    private static final Logger logger = LoggerFactory.getLogger(OidcResource.class);

    @Inject
    private OidcConfig oidcConfig;

    @Inject
    private OidcRequestFactory oidcRequestFactory;

    @Inject
    private OidcValidatorService oidcValidator;

    @Inject
    private WebSessionHelperService sessionHelper;



    @Path("authorize")
    @GET
    public Response authorize(@Context HttpServletRequest request) {
        final HttpSession session = sessionHelper.createNewSession(request);
        State state = oidcRequestFactory.nextState();
        Nonce nonce = oidcRequestFactory.nextNonce();
        URI redirectUri = oidcRequestFactory.newAuthorizeRequest(state, nonce);
        session.setAttribute(SessionAttrs.STATE, state);
        session.setAttribute(SessionAttrs.NONCE, nonce);
        return Response.status(Response.Status.TEMPORARY_REDIRECT).location(redirectUri).build();
    }

    @Path("debug/tokens")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response tokens(@Context HttpServletRequest request) {
        final HttpSession session = sessionHelper.getExistingSession(request);
        if (session != null) {
            TokensDto dto = new TokensDto();
            OIDCTokens tokens = (OIDCTokens) session.getAttribute(SessionAttrs.OIDC_TOKENS);
            dto.setAccessToken(tokens.getAccessToken().getValue());
            dto.setRefreshToken(tokens.getRefreshToken().getValue());
            dto.setIdToken(tokens.getIDToken().getParsedString());
            return Response.status(Response.Status.OK).entity(dto).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @Path("userinfo")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response userInfo(@Context OAuthSecurityContext securityContext, @Context HttpServletRequest request) {
        logger.debug("Security Context: {}", securityContext);
        final HttpSession session = sessionHelper.getExistingSession(request);
        if(session != null) {

            OIDCTokens oidcTokens = (OIDCTokens) session.getAttribute(SessionAttrs.OIDC_TOKENS);
            AccessToken accessToken = oidcTokens.getAccessToken();

            // Create the request
            final UserInfoRequest userInforequest = oidcRequestFactory.newUserInfoRequest(accessToken);

            // Send the request
            HTTPResponse httpResponse;
            try {
                httpResponse = userInforequest.toHTTPRequest().send();
            } catch (IOException e) {
                throw new OidcUserInfoException("Error occurred while performing HTTP request to obtain user info from OIDC provider", e);
            }

            // Parse the response
            UserInfoResponse response;
            try {
                response = UserInfoResponse.parse(httpResponse);
            } catch (ParseException e) {
                throw new OidcUserInfoException("Error occurred while parsing user info received from OIDC provider", e);
            }

            if (response.indicatesSuccess()) {
                // Success: Print user info
                UserInfoDto dto = new UserInfoDto(response.toSuccessResponse().getUserInfo());
                return Response.status(Response.Status.OK).entity(dto).build();
            } else {
                // Error: Print error details
                ErrorObject errorObject = response.toErrorResponse().getErrorObject();
                if ("invalid_token".equals(errorObject.getCode())) {
                    RefreshToken refreshToken = oidcTokens.getRefreshToken();
                    TokenRequest tokenRequest = oidcRequestFactory.newRefreshTokenRequest(refreshToken);

                    TokenResponse parsedTokenResponse;
                    try {
                        HTTPResponse tokenResponse = tokenRequest.toHTTPRequest().send();
                        logger.debug("Token response body: \n {}", tokenResponse.getBody());
                        parsedTokenResponse = TokenResponse.parse(tokenResponse);

                    } catch (IOException | ParseException e) {
                        throw new RuntimeException(e);
                    }

                    if (parsedTokenResponse.indicatesSuccess()) {
                        // Success: Extract the tokens
                        AccessTokenResponse tokens = parsedTokenResponse.toSuccessResponse();
                        Tokens newTokens = tokens.getTokens();
                        accessToken = newTokens.getAccessToken();
                        OIDCTokens newOIDCTokens = new OIDCTokens(newTokens.getAccessToken(), newTokens.getRefreshToken());
                        final HttpSession newSession = sessionHelper.renewSession(request);
                        newSession.setAttribute(SessionAttrs.OIDC_TOKENS, newOIDCTokens);

                        final UserInfoRequest userInfoRequest = oidcRequestFactory.newUserInfoRequest(accessToken);
                        // Parse the response
                        try {
                            httpResponse = userInfoRequest.toHTTPRequest().send();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            response = UserInfoResponse.parse(httpResponse);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        if (response.indicatesSuccess()) {
                            // Success: Print user info
                            UserInfoDto dto = new UserInfoDto(response.toSuccessResponse().getUserInfo());
                            return Response.status(Response.Status.OK).entity(dto).build();
                        }
                    } else {
                        // Error: Handle the error response
                        logger.error("Token request failed: {}", parsedTokenResponse.toErrorResponse().getErrorObject());
                        return Response.status(Response.Status.BAD_REQUEST).entity(response.toErrorResponse().getErrorObject()).build();
                    }
                }
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @Path("logout")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@Context HttpServletRequest request) {
        HttpSession session = sessionHelper.getExistingSession(request);
        if (session != null) {
            OIDCTokens oidcTokens = (OIDCTokens) session.getAttribute(SessionAttrs.OIDC_TOKENS);
            LogoutRequest logoutRequest = oidcRequestFactory.newLogoutRequest(oidcTokens.getIDToken());
            HTTPResponse httpResponse;
            try {
                httpResponse = logoutRequest.toHTTPRequest().send();
            } catch (IOException e) {
                throw new OidcLogoutException("error occurred while performing HTTP request to logout on the OIDC provider", e);
            }
            if(httpResponse.indicatesSuccess()) {
                logger.info("Invalidating session: {}", session.getId());
                session.invalidate();
                NewCookie sessionCookie = sessionHelper.createSessionCookie(0);
                return Response.status(Response.Status.OK).cookie(sessionCookie).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

        }
        return Response.status(Response.Status.OK).build();
    }

    @Path("callback")
    @GET
    public Response callback(@Context HttpServletRequest request, @QueryParam("session_state") String sessionState, @QueryParam("iss") String issuer, @QueryParam("realms") String realms, @QueryParam("code") String code, @QueryParam("state") String nextState) throws URISyntaxException {

        if(!oidcValidator.isValidIssuer(issuer)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        HttpSession session = sessionHelper.getExistingSession(request);

        if (session != null) {
            State initialState = (State) session.getAttribute(SessionAttrs.STATE);

            if(oidcValidator.isValidState(initialState.getValue(), nextState)) {
                session.setAttribute(SessionAttrs.SESSION_STATE, sessionState);
                session.setAttribute(SessionAttrs.AUTH_CODE, code);
            }
            TokenRequest tokenRequest = oidcRequestFactory.newTokenRequest(code);
            TokenResponse response;
            try {
                response = TokenResponse.parse(tokenRequest.toHTTPRequest().send());
            } catch (IOException | ParseException e) {
                throw new OidcCallbackException("error occurred while processing token exchange from OIDC provider", e);
            }
            if (!response.indicatesSuccess()) {
                try {
                    throw new Exception("Token exchange failed: " + response.toErrorResponse().getErrorObject());
                } catch (Exception e) {
                    throw new OidcCallbackException("error occurred while processing token exchange from OIDC provider", e);
                }
            }

            AccessToken accessToken = response.toSuccessResponse().getTokens().getAccessToken();
            RefreshToken refreshToken = response.toSuccessResponse().getTokens().getRefreshToken();
            String idToken = response.toSuccessResponse().getCustomParameters().get("id_token").toString();
            Nonce nonce = (Nonce) session.getAttribute(SessionAttrs.NONCE);
            oidcValidator.validateIdToken(idToken, nonce.getValue());
            OIDCTokens oidcTokens;
            try {
                oidcTokens = OIDCTokens.parse(response.toSuccessResponse().toJSONObject());
            } catch (ParseException e) {
                throw new OidcCallbackException("error occurred while parsing OIDC tokens from OIDC provider", e);
            }
            session.setAttribute(SessionAttrs.ACCESS_TOKEN, accessToken);
            session.setAttribute(SessionAttrs.REFRESH_TOKEN, refreshToken);
            session.setAttribute(SessionAttrs.ID_TOKEN, idToken);
            session.setAttribute(SessionAttrs.OIDC_TOKENS, oidcTokens);
            URI redirectUri = UriBuilder.fromUri(Config.getProperty(Config.Key.FRONTEND_BASE_URL)).build();
            return Response.status(Response.Status.TEMPORARY_REDIRECT).location(redirectUri).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();

    }

}
