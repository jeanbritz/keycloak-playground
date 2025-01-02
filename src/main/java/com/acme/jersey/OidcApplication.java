package com.acme.jersey;

import com.acme.filter.CorsPreflightRequestFilter;
import com.acme.filter.CorsResponseFilter;
import com.acme.jersey.feature.AutoServiceLocatorFeature;
import com.acme.jersey.resource.OidcResource;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

public class OidcApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(OidcResource.class);
        resources.add(AutoServiceLocatorFeature.class);
        resources.add(CorsResponseFilter.class);
        resources.add(CorsPreflightRequestFilter.class);
        return resources;
    }
}
