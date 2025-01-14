package com.acme.hk2.service;

import jakarta.ws.rs.core.HttpHeaders;
import nl.basjes.parse.useragent.UserAgent;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface UserAgentAnalyzer {

    UserAgent parse(HttpHeaders headers);
}
