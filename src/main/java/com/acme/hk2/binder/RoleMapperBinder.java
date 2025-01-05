package com.acme.hk2.binder;

import com.acme.Config;
import com.acme.hk2.mapper.KeycloakRoleMapper;
import com.acme.hk2.mapper.RoleMapper;
import jakarta.inject.Singleton;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class RoleMapperBinder extends AbstractBinder {

    private final String impl;

    public RoleMapperBinder() {
        this.impl = Config.getProperty(Config.Key.OIDC_PROVIDER_NAME);
    }

    @Override
    protected void configure() {
        // For simplicity, let's do a small switch/if-else
        if ("keycloak".equalsIgnoreCase(impl)) {
            bind(KeycloakRoleMapper.class).to(RoleMapper.class).in(Singleton.class);
        } else {
            throw new RuntimeException("Unimplemented user role mapper: " + impl);
        }
    }

}
