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

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import org.springframework.context.annotation.ComponentScan;

/**
 * Represents the application's main view.
 */
@SpringComponent
@UIScope
public final class MainView extends Panel implements View {

    private Label currentHistoryId;
    private Label messagesTotal;
    private Label threadsTotal;
    private Label bodyAvgSize;
    private Label top5Senders;
    private Label top5TitleTags;
    private Label currentUserEmail;

    public MainView() {
        VerticalLayout vlayout = new VerticalLayout();
        vlayout.addStyleName("outlined");
        vlayout.addStyleName("bg");
        vlayout.setSizeFull();
        vlayout.setMargin(true);
        HorizontalLayout hlayout = new HorizontalLayout();
        hlayout.addStyleName("outlined");
        hlayout.setSizeFull();
        setContent(vlayout);

        // Title
        Label caption = new Label("Tempora");
        caption.setStyleName("logo-label", true);
        caption.setWidth(null);
        vlayout.addComponent(caption);
        vlayout.setExpandRatio(caption, 0.2f);

        vlayout.setComponentAlignment(caption, Alignment.MIDDLE_CENTER);
        vlayout.addComponent(hlayout);
        vlayout.setExpandRatio(hlayout, 0.7f);

        //
        // General information about the User's mailbox
        //
        final Panel generalInfoPanel = new Panel("<center>General Information</center>");
        generalInfoPanel.addStyleName("frame-bg-general-info");
        generalInfoPanel.setSizeFull();

        this.currentHistoryId = new Label("0");
        currentHistoryId.setStyleName("general-info-count", true);
        currentHistoryId.setCaption("History ID");

        this.messagesTotal = new Label("0");
        messagesTotal.setStyleName("general-info-count", true);
        messagesTotal.setCaption("Messages Total");

        this.threadsTotal = new Label("0");
        threadsTotal.setCaption("Threads Total");
        threadsTotal.setStyleName("general-info-count", true);

        FormLayout mailboxInfoLayout = new FormLayout(messagesTotal, currentHistoryId, threadsTotal);
        VerticalLayout mailboxInfoMainLayout = new VerticalLayout(mailboxInfoLayout);
        mailboxInfoMainLayout.setSizeFull();
        mailboxInfoMainLayout.setMargin(true);
        generalInfoPanel.setContent(mailboxInfoMainLayout);

        //
        // Stats
        //
        final Panel statsPanel = new Panel("<center>Statistics</center>");
        statsPanel.addStyleName("frame-bg-stats");
        statsPanel.setSizeFull();

        this.bodyAvgSize = new Label("0");
        bodyAvgSize.setCaption("Body Avg. Size");
        bodyAvgSize.setStyleName("stats-count", true);

        FormLayout statsLayout = new FormLayout(bodyAvgSize);
        VerticalLayout statsMainLayout = new VerticalLayout(statsLayout);
        statsMainLayout.setSizeFull();
        statsMainLayout.setMargin(true);
        statsPanel.setContent(statsMainLayout);

        //
        // Top 5
        //
        Panel top5Panel = new Panel("<center>Top 5</center>");
        top5Panel.addStyleName("frame-bg-top5");

        // Top 5 Senders
        Panel top5SendersPanel = new Panel("<center>Senders</center>");
        top5SendersPanel.addStyleName("frame-bg-top5");
        top5SendersPanel.setSizeFull();
        this.top5Senders = new Label("NO DATA", ContentMode.PREFORMATTED);
        top5Senders.setSizeUndefined();
        top5SendersPanel.setContent(top5Senders);

        // Top 5 Title Tags
        Panel top5TitleTagsPanel = new Panel("<center>Title Tags</center>");
        top5TitleTagsPanel.addStyleName("frame-bg-top5");
        top5TitleTagsPanel.setSizeFull();
        this.top5TitleTags = new Label("NO DATA", ContentMode.PREFORMATTED);
        top5TitleTags.setSizeUndefined();
        top5TitleTagsPanel.setContent(top5TitleTags);


        top5Panel.setSizeFull();

        VerticalLayout top5MainLayout = new VerticalLayout(top5SendersPanel, top5TitleTagsPanel);
        top5MainLayout.setMargin(true);
        top5MainLayout.setSpacing(true);
        top5MainLayout.setSizeFull();
        top5MainLayout.setComponentAlignment(top5SendersPanel, Alignment.MIDDLE_CENTER);
        top5MainLayout.setComponentAlignment(top5TitleTagsPanel, Alignment.MIDDLE_CENTER);
        top5Panel.setContent(top5MainLayout);

        hlayout.setSpacing(true);
        hlayout.addComponent(generalInfoPanel);
        hlayout.addComponent(statsPanel);
        hlayout.addComponent(top5Panel);

        this.currentUserEmail = new Label("-");
        currentUserEmail.setCaption("Email");

        Button reloadButton = new Button("\u27F3 Reload");
        reloadButton.addClickListener(e -> {
            getSession().getSession().invalidate();
            getUI().getPage().reload();
        });

        HorizontalLayout infoHLayout = new HorizontalLayout();
        infoHLayout.addStyleName("outlined");
        infoHLayout.setSizeFull();
        infoHLayout.setSpacing(true);
        infoHLayout.setMargin(true);

        infoHLayout.addComponent(reloadButton);
        reloadButton.setWidth(null);
        infoHLayout.setComponentAlignment(reloadButton, Alignment.BOTTOM_LEFT);

        infoHLayout.addComponent(currentUserEmail);
        currentUserEmail.setWidth(null);
        infoHLayout.setComponentAlignment(currentUserEmail, Alignment.BOTTOM_RIGHT);

        vlayout.addComponent(infoHLayout);
        vlayout.setExpandRatio(infoHLayout, 0.5f);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }

    public Label getCurrentHistoryId() {
        return currentHistoryId;
    }

    public Label getMessagesTotal() {
        return messagesTotal;
    }

    public Label getThreadsTotal() {
        return threadsTotal;
    }

    public Label getBodyAvgSize() {
        return bodyAvgSize;
    }

    public Label getTop5Senders() {
        return top5Senders;
    }

    public Label getTop5TitleTags() {
        return top5TitleTags;
    }

    public Label getCurrentUserEmail() {
        return currentUserEmail;
    }

}
