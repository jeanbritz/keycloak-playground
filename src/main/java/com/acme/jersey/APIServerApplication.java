package com.acme.jersey;

import com.acme.filter.CorsPreflightRequestFilter;
import com.acme.filter.CorsResponseFilter;
import com.acme.jersey.resource.MovieResource;
import com.acme.jersey.feature.AutoServiceLocatorFeature;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

public class APIServerApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(MovieResource.class); // Add your Jersey resources here
        resources.add(AutoServiceLocatorFeature.class);
        resources.add(CorsResponseFilter.class);
        resources.add(CorsPreflightRequestFilter.class);
        return resources;
    }
}
