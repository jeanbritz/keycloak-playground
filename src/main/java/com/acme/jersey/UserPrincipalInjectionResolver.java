package com.acme.jersey;

import com.acme.jakarta.annotation.UserPrincipal;
import jakarta.inject.Provider;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

public class UserPrincipalInjectionResolver extends ParamInjectionResolver<UserPrincipal> {

    public UserPrincipalInjectionResolver(ValueParamProvider valueParamProvider, Class<UserPrincipal> annotation, Provider<ContainerRequest> request) {
        super(valueParamProvider, annotation, request);
    }



}