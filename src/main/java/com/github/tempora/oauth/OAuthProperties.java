/* 
 * Copyright 2017 Faissal Elamraoui.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tempora.oauth;

import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@SpringComponent
@ConfigurationProperties(prefix = "oauth2", ignoreUnknownFields = true)
public class OAuthProperties {

    private String clientId;
    private String clientSecret;
    private String userAuthorizationUri;
    private String scopes;
    private String redirectUri;
    private String redirectPath;
    private String accessTokenUri;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getUserAuthorizationUri() {
        return userAuthorizationUri;
    }

    public void setUserAuthorizationUri(String userAuthorizationUri) {
        this.userAuthorizationUri = userAuthorizationUri;
    }

    public Collection<String> getScopes() {
        return scopes == null ? Collections.emptyList() : Arrays.asList(scopes.split(","));
    }

    public void setScopes(Collection<String> scopes) {
        this.scopes = scopes == null ? null : String.join(",", scopes);
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getRedirectPath() {
        return redirectPath;
    }

    public void setRedirectPath(String redirectPath) {
        this.redirectPath = redirectPath;
    }

    public String getAccessTokenUri() {
        return accessTokenUri;
    }

    public void setAccessTokenUri(String accessTokenUri) {
        this.accessTokenUri = accessTokenUri;
    }


    @Override
    public String toString() {
        return "OAuthProperties{" +
                "clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", userAuthorizationUri='" + userAuthorizationUri + '\'' +
                ", scopes='" + scopes + '\'' +
                ", redirectUri='" + redirectUri + '\'' +
                ", redirectPath='" + redirectPath + '\'' +
                ", accessTokenUri='" + accessTokenUri + '\'' +
                '}';
    }

}