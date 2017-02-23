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

import com.github.tempora.svc.Utilities;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.Profile;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@SpringComponent
@Scope(value="singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CurrentUser implements Serializable {

    private static final String APPLICATION_NAME = "Tempora";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static HttpTransport HTTP_TRANSPORT = null;

    private final OAuthProperties oauthProperties;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public CurrentUser(OAuthProperties oauthProperties) {
        this.oauthProperties = oauthProperties;
    }

    private String authorizationCode;
    private String accessToken;

    private transient GoogleCredential googleCredential;
    private transient Profile profile;

    public boolean isAuthenticated() {
        return authorizationCode != null;
    }

    public void setAuthorizationCode(String authorizationCode) {
        if (authorizationCode != null) {
            try {
                TokenResponse response = new AuthorizationCodeTokenRequest(HTTP_TRANSPORT, JSON_FACTORY,
                        new GenericUrl(oauthProperties.getAccessTokenUri()), authorizationCode)
                        .setClientAuthentication(new ClientParametersAuthentication(oauthProperties.getClientId(),
                                oauthProperties.getClientSecret()))
                        .setScopes(oauthProperties.getScopes())
                        .setRedirectUri(oauthProperties.getRedirectUri())
                        .execute();

                this.accessToken = response.getAccessToken();
                this.authorizationCode = authorizationCode;
            } catch (Exception ex) {
                LoggerFactory.getLogger(getClass()).error("Error retrieving access token", ex);
            }
        } else {
            this.accessToken = null;
            this.authorizationCode = null;
            this.googleCredential = null;
            this.profile = null;
        }
    }

    public Optional<GoogleCredential> getCredential() {
        if (googleCredential == null && accessToken != null) {
            googleCredential = new GoogleCredential().setAccessToken(accessToken);
        }
        return Optional.ofNullable(googleCredential);
    }

    private Gmail getGmailService(GoogleCredential credential) {
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public Optional<Profile> getProfile() {
        if (profile == null) {
            profile = getCredential().map(credential -> {
                Gmail gmail = getGmailService(credential);
                try {
                    return gmail.users().getProfile("me").execute();
                } catch (Exception ex) {
                    LoggerFactory.getLogger(getClass()).error("Error retrieving user profile", ex);
                    return null;
                }
            }).orElse(null);
        }
        return Optional.ofNullable(profile);
    }

    public Optional<List<Message>> getListMessages(Date after, Date before) {
        List<Message> responseList = getCredential().map(credential -> {
            Gmail gmail = getGmailService(credential);
            List<Message> messagesList = null;
            ListMessagesResponse response = null;
            try {
                Gmail.Users.Messages.List request = gmail.users().messages().list("me")
                        .setQ(Utilities.searchByDateQuery(after, before))
                        .setMaxResults(50l)
                        .setLabelIds(Arrays.asList("INBOX"));

                messagesList = new LinkedList<>();
                do {
                    response = request.execute();
                    messagesList.addAll(response.getMessages());
                    request.setPageToken(response.getNextPageToken());
                } while(request.getPageToken() != null && request.getPageToken().length() > 0);
            } catch (IOException ex) {
                LoggerFactory.getLogger(getClass()).error("Error while getting list of messages", ex);
                return null;
            }
            return messagesList;
        }).orElse(null);
        return Optional.ofNullable(responseList);
    }

    public Optional<List<Message>> getListMessages() {
        List<Message> responseList = getCredential().map(credential -> {
            Gmail gmail = getGmailService(credential);
            List<Message> messagesList = null;
            ListMessagesResponse response = null;
            try {
                Gmail.Users.Messages.List request = gmail.users().messages().list("me")
                        .setMaxResults(50l)
                        .setLabelIds(Arrays.asList("INBOX"));

                messagesList = new LinkedList<>();
                do {
                    response = request.execute();
                    messagesList.addAll(response.getMessages());
                    request.setPageToken(response.getNextPageToken());
                } while(request.getPageToken() != null && request.getPageToken().length() > 0);
            } catch (IOException ex) {
                LoggerFactory.getLogger(getClass()).error("Error while getting list of messages", ex);
                return null;
            }
            return messagesList;
        }).orElse(null);
        return Optional.ofNullable(responseList);
    }

    public Optional<Message> getFullyQualifiedMessage(String msgId) {
        Message msg = getCredential().map(credential -> {
            Gmail gmail = getGmailService(credential);
            try {
                return gmail.users().messages().get("me", msgId).execute();
            } catch (IOException ex) {
                LoggerFactory.getLogger(getClass()).error("Error while getting the message", ex);
                return null;
            }
        }).orElse(null);
        return Optional.ofNullable(msg);
    }

    /**
     * Returns the estimation of messages total.
     * @return the messages total.
     */
    public Integer getMessagesTotal() {
        Profile currentProfile = getProfile().get();
        if (currentProfile != null) {
            return currentProfile.getMessagesTotal();
        }
        return 0;
    }

}
