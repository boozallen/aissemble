package com.boozallen.aissemble.alerting.teams;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Alerting::Teams
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.alerting.teams.models.Card;
import com.boozallen.aissemble.alerting.teams.models.CardBodyEntry;
import com.boozallen.aissemble.alerting.teams.models.CardContent;
import com.boozallen.aissemble.alerting.teams.models.CardMessage;
import com.boozallen.aissemble.alerting.teams.models.entries.TextEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates an Adaptive Card for Teams Integration.
 * See https://docs.microsoft.com/en-us/microsoftteams/platform/task-modules-and-cards/cards/cards-reference for
 * more details.
 */
public class CardMessageFactory {

    /**
     * Create a default adaptive card for Teams integration with no body.
     * @return the configured card
     */
    public static CardMessage create() {
        final CardMessage cardMessage = new CardMessage();
        cardMessage.setType("message");

        final Card card = new Card();
        card.setContentType("application/vnd.microsoft.card.adaptive");

        final CardContent cardContent = new CardContent();
        cardContent.set$schema("http://adaptivecards.io/schemas/adaptive-card.json");
        cardContent.setType("AdaptiveCard");
        cardContent.setVersion("1.2");

        final List<CardBodyEntry> bodyEntries = new ArrayList<>();

        cardContent.setBody(bodyEntries);
        card.setContent(cardContent);

        final List<Card> attachments = new ArrayList<>();
        attachments.add(card);

        cardMessage.setAttachments(attachments);

        card.setContent(cardContent);

        return cardMessage;
    }

    /**
     * Create an adaptive card for Teams integration
     * @param message the message to send
     * @return the configured card with the message
     */
    public static CardMessage create(String message) {
        final CardMessage cardMessage = create();

        final TextEntry entry = new TextEntry();
        entry.setText(message);

        final List<CardBodyEntry> bodyEntries = new ArrayList<>();

        bodyEntries.add(entry);

        CardContent cardContent = cardMessage.getAttachments().get(0).getContent();
        List<CardBodyEntry> body = cardContent.getBody();
        body.addAll(bodyEntries);

        return cardMessage;
    }
}
