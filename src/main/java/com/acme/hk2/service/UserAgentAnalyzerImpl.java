package com.acme.hk2.service;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import nl.basjes.parse.useragent.UserAgent;
import org.eclipse.jetty.server.Authentication;
import org.glassfish.jersey.server.ContainerRequest;
import org.jvnet.hk2.annotations.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserAgentAnalyzerImpl implements UserAgentAnalyzer {

    private final nl.basjes.parse.useragent.UserAgentAnalyzer analyzer;

    public UserAgentAnalyzerImpl() {
        analyzer = nl.basjes.parse.useragent.UserAgentAnalyzer.newBuilder()
                .hideMatcherLoadStats()
                .withCache(10000)
                .build();
    }

    @Override
    public UserAgent parse(ContainerRequest request) {
        Map<String, String> headers = new HashMap<>();
        MultivaluedMap<String, String> containerHeaders = request.getRequestHeaders();
        for(Map.Entry<String, List<String>> header : containerHeaders.entrySet()) {
            headers.put(header.getKey(), String.join(",", header.getValue()));
        }
        return analyzer.parse(headers);
    }
}
