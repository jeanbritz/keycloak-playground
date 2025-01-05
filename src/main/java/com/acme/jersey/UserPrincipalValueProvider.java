package com.acme.jersey;

import com.acme.jakarta.annotation.UserPrincipal;
import jakarta.ws.rs.core.SecurityContext;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

import java.security.Principal;
import java.util.function.Function;


public class UserPrincipalValueProvider implements ValueParamProvider {

    @Override
    public Function<ContainerRequest, ?> getValueProvider(Parameter parameter) {
        if (parameter.isAnnotationPresent(UserPrincipal.class)) {
            return new UserPrincipalSupplier(parameter);
        }
        return null; // Not our annotation
    }

    @Override
    public PriorityType getPriority() {
        return Priority.NORMAL;
    }

    private static class UserPrincipalSupplier implements Function<ContainerRequest, Object> {

        private final Parameter parameter;

        public UserPrincipalSupplier(Parameter parameter) {
            this.parameter = parameter;
        }

        @Override
        public Object apply(ContainerRequest containerRequest) {
            final Class<?> rawType = this.parameter.getRawType();
            SecurityContext securityContext = containerRequest.getSecurityContext();
            if(Principal.class.isAssignableFrom(rawType)) {
                if (securityContext != null && securityContext.getUserPrincipal() != null) {
                    return securityContext.getUserPrincipal();
                }
            }
            return null;

        }
    }
}
