package com.acme.jakarta.resource.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError implements Serializable {

    @JsonProperty("error")
    private final String error;

    @JsonProperty("details")
    private List<ApiErrorMessage> details;

    public ApiError(String error) {
        this.error = error;
    }

    public ApiError(String error, List<ApiErrorMessage> details) {
        this.error = error;
        this.details = details;
    }

    public void setDetails(List<ApiErrorMessage> details) {
        this.details = details;
    }

    public void addDetail(ApiErrorMessage detail) {
        if (this.details == null) {
            this.details = new ArrayList<>();
        }
        this.details.add(detail);
    }

    public static class ApiErrorMessage {
        @JsonProperty("field")
        private final String field;
        @JsonProperty("reason")
        private final String reason;

        public ApiErrorMessage(String field, String reason) {
            this.field = field;
            this.reason = reason;
        }

        public String getField() {
            return field;
        }

        public String getReason() {
            return reason;
        }
    }
}
