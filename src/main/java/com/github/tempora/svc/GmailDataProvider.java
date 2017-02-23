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
package com.github.tempora.svc;

import com.github.tempora.oauth.CurrentUser;
import com.google.api.services.gmail.model.*;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import javax.rmi.CORBA.Util;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringComponent
public class GmailDataProvider {

    @Autowired
    CurrentUser currentUser;

    public List<GMessage> getQueryMessagesList(Date after, Date before) {
        Optional<List<Message>> list = currentUser.getListMessages(after, before);
        if (!list.isPresent()) return null;
        return digestMessagesList(list.get());
    }

    public List<GMessage> getMessagesList() {
        Optional<List<Message>> list = currentUser.getListMessages();
        if (!list.isPresent()) return null;
        return digestMessagesList(list.get());
    }

    private List<GMessage> digestMessagesList(List<Message> messages) {
        return messages.stream().map(msg -> {
            GMessage message = new GMessage();
            message.setId(msg.getId());
            return message;
        }).map(msg -> {
            Optional<Message> remoteMsg = currentUser.getFullyQualifiedMessage(msg.getId());
            if (!remoteMsg.isPresent()) return null;

            for (MessagePartHeader header : remoteMsg.get().getPayload().getHeaders()) {
                switch (header.getName()) {
                    case "Date": {
                        try {
                            msg.setDate(Utilities.str2date(header.getValue()));
                        } catch (ParseException e) {
                            LoggerFactory.getLogger(getClass()).error("error while parsing email date", e);
                        }
                        break;
                    }
                    case "To":
                        msg.setTo(header.getValue());
                        break;
                    case "From":
                        msg.setFrom(header.getValue());
                        break;
                    case "Subject":
                        msg.setSubject(header.getValue());
                        break;
                }
            }

            msg.setBody(remoteMsg.get().getSnippet());
            msg.setBodySize(remoteMsg.get().getSizeEstimate());

            return msg;
        }).collect(Collectors.toList());
    }

}
