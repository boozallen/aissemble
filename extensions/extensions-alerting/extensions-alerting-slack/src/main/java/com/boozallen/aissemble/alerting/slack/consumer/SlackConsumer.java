package com.boozallen.aissemble.alerting.slack.consumer;

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

import jakarta.enterprise.context.ApplicationScoped;

import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aissemble.alerting.core.Alert;
import com.boozallen.aissemble.alerting.slack.SlackClient;
import com.boozallen.aissemble.alerting.slack.config.SlackConfig;

@ApplicationScoped
public class SlackConsumer {
    private static final Logger logger = LoggerFactory.getLogger(SlackConsumer.class);

    private static SlackConfig slackConfig = KrauseningConfigFactory.create(SlackConfig.class);

    @Incoming("alerts")
    public void sendSlackNotification(Alert alert) {
        if (slackConfig.isSlackAlertEnabled() && StringUtils.isNotBlank(slackConfig.getSlackToken())) {
            logger.info("Sending alert to slack channel {}", slackConfig.getSlackChannel());
            SlackClient.sendSlackMessage(alert, slackConfig.getClientHostId());
        } else {
            logger.warn("Slack alerts are disabled or not currently configured. No slack alert will be sent.");
        }
    }
}
