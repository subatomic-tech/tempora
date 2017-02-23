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
package com.github.tempora.view;

import com.github.tempora.oauth.CurrentUser;
import com.github.tempora.svc.GMessage;
import com.github.tempora.svc.GmailDataProvider;
import com.google.api.services.gmail.model.Profile;
import com.vaadin.annotations.*;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Theme("tempora")
@Title("Tempora")
@SpringUI
@Push(transport = Transport.WEBSOCKET)
public class TemporaUI extends UI {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemporaUI.class);
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    @Autowired
    CurrentUser currentUser;

    @Autowired
    MainView mainView;

    @Autowired
    GmailDataProvider gmailDataProvider;

    private ScheduledFuture<?> top5JobHandle;
    private ScheduledFuture<?> generalInfoJobHandle;

    private Runnable updateTop5Job = () -> access(() -> {
        DateTime today = new DateTime();
        List<GMessage> messagesList = gmailDataProvider.getQueryMessagesList(today.minusDays(1).toDate(), today.toDate());
        String top5senders = messagesList.parallelStream()
                .map(msg -> msg.getFrom()) // Map the list to senders
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String,Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> e.getKey())
                .collect(Collectors.joining("\n"));

        if (!top5senders.isEmpty()) {
            mainView.getTop5Senders().setValue(top5senders);
            notifyEndUser("Top 5 Senders Updated");
        }

        String top5TitleTags = messagesList.parallelStream()
                .map(msg -> {
                    String subject = msg.getSubject();
                    if (subject != null) {
                        Pattern p = Pattern.compile("\\[(.*?)\\]");
                        Matcher m = p.matcher(subject);
                        try {
                            if (m.find()) {
                                String theTag = m.group();
                                return theTag.replace("[", "").replace("]", "");
                            }
                        } catch (IllegalStateException ex) {
                            return "";
                        }
                    }
                    return "";
                }).collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String,Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> e.getKey())
                .collect(Collectors.joining("\n"));
        if (!top5TitleTags.isEmpty()) {
            mainView.getTop5TitleTags().setValue(top5TitleTags);
            notifyEndUser("Top 5 Tag Titles Updated");
        }

        OptionalDouble avgBodySize = messagesList.parallelStream()
                .mapToInt(msg -> msg.getBodySize())
                .average();
        if (avgBodySize.isPresent()) {
            mainView.getBodyAvgSize().setValue(String.format("%.2f", avgBodySize.getAsDouble()));
            notifyEndUser("Statistics Updated");
        }
    });

    private Runnable updateGeneralInfoJob = () -> access(() -> {
        Profile profile = currentUser.getProfile().get();
        if (profile == null) return;
        mainView.getCurrentUserEmail().setValue(profile.getEmailAddress());
        mainView.getCurrentHistoryId().setValue(profile.getHistoryId().toString());
        mainView.getMessagesTotal().setValue(profile.getMessagesTotal().toString());
        mainView.getThreadsTotal().setValue(profile.getThreadsTotal().toString());
        notifyEndUser("General Information Updated");
    });


    @Override
    protected void init(VaadinRequest request) {
        setLocale(Locale.US);
        Responsive.makeResponsive(this);
        addStyleName(ValoTheme.UI_WITH_MENU);

        setContent(mainView);

        // Update the user profile
        Profile profile = currentUser.getProfile().get();
        if (profile == null) return;

        mainView.getCurrentUserEmail().setValue(profile.getEmailAddress());
        mainView.getCurrentHistoryId().setValue(profile.getHistoryId().toString());
        mainView.getMessagesTotal().setValue(profile.getMessagesTotal().toString());
        mainView.getThreadsTotal().setValue(profile.getThreadsTotal().toString());

        // Using Vaadin Push to fetch the Mailbox statistics
        // out of RDF4J store
        top5JobHandle = executorService.scheduleWithFixedDelay(updateTop5Job,
                500,
                3000,
                TimeUnit.MILLISECONDS);
        generalInfoJobHandle = executorService.scheduleWithFixedDelay(updateGeneralInfoJob,
                500,
                3000,
                TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    void destroy() {
        LOGGER.info("Canceling background jobs");
        top5JobHandle.cancel(true);
        generalInfoJobHandle.cancel(true);
    }

    /**
     * Helper method to display a notification to the end user.
     *
     * @param message the notification description.
     */
    private void notifyEndUser(String message) {
        Notification notification = new Notification("INFO",
                message,
                Notification.Type.TRAY_NOTIFICATION,
                true);
        notification.setPosition(Position.BOTTOM_RIGHT);
        notification.show(getPage());
    }

}
