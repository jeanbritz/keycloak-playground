package com.acme.hk2.binder;

import com.acme.jakarta.annotation.UserPrincipal;
import com.acme.jersey.UserPrincipalInjectionResolver;
import com.acme.jersey.UserPrincipalValueProvider;
import jakarta.inject.Singleton;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

public class UserPrincipalBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(UserPrincipalValueProvider.class)
                .to(ValueParamProvider.class)
                .in(Singleton.class);

        bind(UserPrincipalInjectionResolver.class)
                .to(new TypeLiteral<ParamInjectionResolver<UserPrincipal>>() {})
                .in(Singleton.class);
    }
}
