package com.acme.hk2.service;

import com.acme.Config;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakRoleMapper implements RoleMapper {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakRoleMapper.class);

    @Override
    public List<String> map(JWT jwt) {
        try {
            JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();
            Map<String, Object> resourceAccess = claimsSet.getJSONObjectClaim("resource_access");
            if(resourceAccess instanceof Map<String, Object>){
                Map<String, Object> account = (Map<String, Object>) resourceAccess.get(Config.getProperty(Config.Key.OIDC_CLIENT_ID));
                List<String> roles = (List<String>)account.get("roles");
                logger.debug("Roles mapped from JWT token: {}", roles);
                return roles;
            }
            return Collections.emptyList();

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
