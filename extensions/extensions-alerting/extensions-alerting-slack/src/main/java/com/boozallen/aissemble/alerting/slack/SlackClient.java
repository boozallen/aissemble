package com.boozallen.aissemble.alerting.slack;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Alerting::Slack
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

import org.aeonbits.owner.KrauseningConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aissemble.alerting.core.Alert;
import com.boozallen.aissemble.alerting.core.Alert.Status;
import com.boozallen.aissemble.alerting.slack.config.SlackConfig;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

public class SlackClient {

    private static final Logger logger = LoggerFactory.getLogger(SlackClient.class);

    private static SlackConfig slackConfig = KrauseningConfigFactory.create(SlackConfig.class);

    private static Slack slack = Slack.getInstance();

    public static ChatPostMessageResponse sendSlackMessage(Alert alert, String senderId) {
        assert senderId != null: "Sender ID is required";

        StringBuilder builder = new StringBuilder();
        if (Status.FAILURE.equals(alert.getStatus())) {
            builder.append(slackConfig.getDefaultFailureIcon());
        } else {
            builder.append(slackConfig.getDefaultSuccessIcon());
        }
        builder.append(alert.getMessage());
        return sendSlackMessage(builder.toString(), senderId);
    }

    public static ChatPostMessageResponse sendSlackMessage(String message, String senderId) {
        assert senderId != null: "Sender ID is required";

        ChatPostMessageResponse response = null;

        try {
            // Initialize an API Methods client with the given token
            MethodsClient methods = slack.methods(slackConfig.getSlackToken());

            //Message with sender id
            String messageWitId = String.format("%s (Sent by: %s)", message, senderId);

            // Build a request object
            ChatPostMessageRequest request = ChatPostMessageRequest.builder().channel(slackConfig.getSlackChannel())
                    .username(senderId) // This doesn't have a visible effect on the Slack message, but does show in the logs i.e. toString.
                    .text(messageWitId).build();
            
            response = methods.chatPostMessage(request);
        } catch (Exception e) {
            logger.error("Encountered error while trying to alert slack channel {}: {}", slackConfig.getSlackChannel(),
                    e);
        }

        return response;
    }

}
