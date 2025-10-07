package com.boozallen.aissemble.alerting.slack.config;

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
