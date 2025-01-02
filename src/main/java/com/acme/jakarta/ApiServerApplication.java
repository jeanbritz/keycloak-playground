package com.acme.jakarta;

import com.acme.jakarta.filter.CorsPreflightRequestFilter;
import com.acme.jakarta.filter.CorsResponseFilter;
import com.acme.jakarta.filter.OAuthAuthorizationFilter;
import com.acme.jakarta.resource.MovieResource;
import com.acme.jakarta.feature.AutoServiceLocatorFeature;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import java.util.HashSet;
import java.util.Set;

public class ApiServerApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(MovieResource.class);
        resources.add(AutoServiceLocatorFeature.class);
        resources.add(CorsPreflightRequestFilter.class);
        resources.add(CorsResponseFilter.class);
        resources.add(OAuthAuthorizationFilter.class);
        resources.add(RolesAllowedDynamicFeature.class);
        return resources;
    }
}
