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

import java.util.Date;

/**
 * Wrapper for {@link com.google.api.services.gmail.model.Message}.
 */
public class GMessage {

    private String id;
    private Date date;
    private String subject;
    private String from;
    private String to;
    private String body;
    private Integer bodySize;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public Integer getBodySize() {
        return bodySize;
    }

    public void setBodySize(Integer bodySize) {
        this.bodySize = bodySize;
    }

    @Override
    public String toString() {
        return "GMessage{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", subject='" + subject + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", body='" + body + '\'' +
                ", bodySize=" + bodySize +
                '}';
    }

}
