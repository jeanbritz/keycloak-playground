package com.acme.jakarta.feature;

import com.acme.jakarta.filter.RolesAllowedRequestFilter;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import org.glassfish.jersey.server.model.AnnotatedMethod;

import java.util.List;


public class RolesAllowedDynamicFeature implements DynamicFeature {
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
        AnnotatedMethod am = new AnnotatedMethod(resourceInfo.getResourceMethod());
        if (am.isAnnotationPresent(DenyAll.class)) {
            featureContext.register(new RolesAllowedRequestFilter());
        } else {
            RolesAllowed ra = am.getAnnotation(RolesAllowed.class);
            if (ra != null) {
                featureContext.register(new RolesAllowedRequestFilter(List.of(ra.value())));
            } else if (!am.isAnnotationPresent(PermitAll.class)) {
                ra = resourceInfo.getResourceClass().getAnnotation(RolesAllowed.class);
                if (ra != null) {
                    featureContext.register(new RolesAllowedRequestFilter(List.of(ra.value())));
                }
            }
        }
    }
}
