package com.acme.hk2.service;

import nl.basjes.parse.useragent.UserAgent;
import org.glassfish.jersey.server.ContainerRequest;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface UserAgentAnalyzer {

    UserAgent parse(ContainerRequest request);
}
