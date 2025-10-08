package com.boozallen.aissemble.alerting.teams;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Alerting::Teams
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
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
 * #L%
 */

import com.boozallen.aissemble.alerting.teams.models.CardMessage;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service to send messages to Microsoft Teams
 */
@ApplicationScoped
public class TeamsMessageService {
    private static final String SUCCESS_CODE = "1";
    private TeamsClient teamsClient;

    // Default no-args constructor for CDI to support injection
    TeamsMessageService() { }

    @Inject
    public TeamsMessageService(@RestClient final TeamsClient teamsClient) {
        this.teamsClient = teamsClient;
    }

    /**
     * Sends a message to the configured Microsoft Teams channel.
     * @param message the message to send
     * @return true if message is sent successfully, false otherwise
     */
    public boolean sendMessage(final String message) {
        final CardMessage cardMessage = CardMessageFactory.create(message);
        return sendMessage(cardMessage);
    }

    /**
     * Sends a message to the configured Microsoft Teams Channel
     * @param cardMessage the configured card message to send
     * @return true if message is sent successfully, false otherwise
     */
    public boolean sendMessage(final CardMessage cardMessage) {
        return SUCCESS_CODE.equalsIgnoreCase(teamsClient.sendMessage(cardMessage));
    }

}
