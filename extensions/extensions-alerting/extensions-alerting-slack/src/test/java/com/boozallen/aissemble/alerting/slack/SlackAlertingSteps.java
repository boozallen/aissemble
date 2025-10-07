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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import com.boozallen.aissemble.alerting.slack.config.SlackConfig;
import org.aeonbits.owner.KrauseningConfigFactory;

import com.boozallen.aissemble.alerting.core.Alert;
import com.boozallen.aissemble.alerting.core.Alert.Status;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class SlackAlertingSteps {
    private final String MESSAGE = "Test slack message, please ignore.";

    private ChatPostMessageResponse slackResponse;

    private static SlackConfig slackConfig = KrauseningConfigFactory.create(SlackConfig.class);

    @When("an alert is sent to slack")
    public void an_alert_is_sent_to_slack() {

        Alert alert = new Alert();
        alert.setId(UUID.randomUUID());
        alert.setStatus(Status.FAILURE);
        alert.setMessage(MESSAGE);
        ChatPostMessageResponse response = new ChatPostMessageResponse();
        response.setOk(true);
        try (MockedStatic<SlackClient> mockedSlackClient = Mockito.mockStatic(SlackClient.class)) {
            mockedSlackClient.when(() -> SlackClient.sendSlackMessage(Mockito.any(Alert.class), Mockito.anyString())).thenCallRealMethod();
            mockedSlackClient.when(() -> SlackClient.sendSlackMessage(getMessage(alert), slackConfig.getClientHostId())).thenReturn(response);

            slackResponse = SlackClient.sendSlackMessage(alert, slackConfig.getClientHostId());
        }
    }

    @Then("the alert is sent to the configured slack channel successfully")
    public void the_alert_is_sent_to_the_configured_slack_channel_successfully() {
        assertNotNull("Slack response was unexpectedly null", slackResponse);
        assertTrue("slack message was sent ok", slackResponse.isOk());
    }

    private String getMessage(Alert alert) {
        StringBuilder builder = new StringBuilder();
        builder.append(slackConfig.getDefaultFailureIcon());
        builder.append(alert.getMessage());
        return builder.toString();
    }

}
