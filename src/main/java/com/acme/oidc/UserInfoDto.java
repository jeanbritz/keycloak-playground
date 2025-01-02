package com.acme.oidc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import net.minidev.json.JSONObject;

public class UserInfoDto {

    public UserInfoDto(UserInfo userInfo) {
        this.sub = userInfo.getSubject().getValue();
        this.name = userInfo.getName();
        this.preferredUsername = userInfo.getPreferredUsername();
        this.givenName = userInfo.getGivenName();
        this.familyName = userInfo.getFamilyName();
        this.emailAddress = userInfo.getEmailAddress();
    }

    @JsonProperty("sub")
    private String sub;

    @JsonProperty("name")
    private String name;

    @JsonProperty("preferred_username")
    private String preferredUsername;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    @JsonProperty("email")
    private String emailAddress;

    public String getSubject() {
        return sub;
    }

    public String getName() {
        return name;
    }

    public String getPreferredUsername() {
        return preferredUsername;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
