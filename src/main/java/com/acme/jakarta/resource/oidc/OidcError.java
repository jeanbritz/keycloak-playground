package com.acme.jakarta.resource.oidc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class OidcError {

    @JsonProperty("code")
    private String code;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status_code")
    private int httpStatusCode;

    /**
     * Optional custom parameters, empty or {@code null} if none.
     */
    @JsonProperty("custom_params")
    private Map<String,String> customParams;

    public OidcError(String code, String description, int httpStatusCode) {
        this.code = code;
        this.description = description;
        this.httpStatusCode = httpStatusCode;
    }
}
