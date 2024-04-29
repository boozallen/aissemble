package com.boozallen.aissemble.alerting.slack.config;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Alerting::Slack
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.aeonbits.owner.KrauseningConfig;

@KrauseningConfig.KrauseningSources("slack-integration.properties")
public interface SlackConfig extends KrauseningConfig {

    /**
     * The slack channel to message.
     */
    @Key("slack.channel")
    @DefaultValue("aiops-alerts")
    String getSlackChannel();

    /**
     * Whether the slack alerts are enabled.
     */
    @Key("slack.alert.enabled")
    @DefaultValue("false")
    Boolean isSlackAlertEnabled();
       
    /**
     * The slack token to use.
     */
    @Key("slack.token")
    String getSlackToken();
    
    /**
     * Default icon for success notifications.
     */
    @Key("slack.success.icon")
    @DefaultValue(":white_check_mark:    ")
    String getDefaultSuccessIcon();

    /**
     * Default icon for failure notifications.
     */
    @Key("slack.failure.message")
    @DefaultValue(":warning:*    ACTION REQUIRED*    :warning:\n")
    String getDefaultFailureIcon();

    /**
     * Identifier for the sending machine.
     */
    @Key("slack.client.host.id")
    @DefaultValue("host id not set")
    String getClientHostId();
}
