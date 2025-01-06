package com.acme.jakarta;

import com.acme.hk2.binder.RoleMapperBinder;
import com.acme.jakarta.filter.CorsPreflightRequestFilter;
import com.acme.jakarta.filter.CorsResponseFilter;
import com.acme.jakarta.feature.AutoServiceLocatorFeature;
import com.acme.jakarta.filter.UserAgentSessionFilter;
import com.acme.jakarta.mapper.InvalidSessionExceptionMapper;
import com.acme.jakarta.mapper.OidcCallbackExceptionMapper;
import com.acme.jakarta.resource.oidc.OidcResource;
import com.acme.jersey.monitoring.JerseyApplicationEventListener;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/oidc")
public class OidcApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        // Resources
        classes.add(OidcResource.class);
        // Features
        classes.add(AutoServiceLocatorFeature.class);
        // Binders
        classes.add(RoleMapperBinder.class);
        // Filters
        classes.add(CorsResponseFilter.class);
        classes.add(CorsPreflightRequestFilter.class);
        classes.add(UserAgentSessionFilter.class);
        // Listener
        classes.add(JerseyApplicationEventListener.class);
        // Exception Mappers
        classes.add(OidcCallbackExceptionMapper.class);
        classes.add(InvalidSessionExceptionMapper.class);

        return classes;
    }


}
