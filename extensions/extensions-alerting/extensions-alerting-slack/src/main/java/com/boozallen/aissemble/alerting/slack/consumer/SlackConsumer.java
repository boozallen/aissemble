package com.boozallen.aissemble.alerting.slack.consumer;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Alerting::Slack
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import javax.enterprise.context.ApplicationScoped;

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
