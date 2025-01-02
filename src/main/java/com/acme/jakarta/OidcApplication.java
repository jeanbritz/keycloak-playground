package com.acme.jakarta;

import com.acme.jakarta.filter.CorsPreflightRequestFilter;
import com.acme.jakarta.filter.CorsResponseFilter;
import com.acme.jakarta.feature.AutoServiceLocatorFeature;
import com.acme.jakarta.resource.OidcResource;
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
