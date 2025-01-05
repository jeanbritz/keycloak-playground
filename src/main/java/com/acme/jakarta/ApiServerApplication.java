package com.acme.jakarta;

import com.acme.Config;
import com.acme.jakarta.feature.RolesAllowedDynamicFeature;
import com.acme.jakarta.filter.CorsPreflightRequestFilter;
import com.acme.jakarta.filter.CorsResponseFilter;
import com.acme.jakarta.filter.UserAgentSessionFilter;
import com.acme.jakarta.resource.api.MovieResource;
import com.acme.jakarta.feature.AutoServiceLocatorFeature;
import com.acme.jakarta.resource.api.OpenApiResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;


import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class ApiServerApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        // Resources
        classes.add(MovieResource.class);
        if(Config.getBoolProperty(Config.Key.SWAGGER_ENABLED)) {
            classes.add(OpenApiResource.class);
        }
        // Features
        classes.add(AutoServiceLocatorFeature.class);
        classes.add(RolesAllowedDynamicFeature.class);

        // Filters
        classes.add(CorsPreflightRequestFilter.class);
        classes.add(CorsResponseFilter.class);
        classes.add(UserAgentSessionFilter.class);


        return classes;
    }
}
