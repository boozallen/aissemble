package com.boozallen.aissemble.alerting.teams.config;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Alerting::Teams
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.aeonbits.owner.KrauseningConfig;

@KrauseningConfig.KrauseningSources("teams-integration.properties")
public interface TeamsConfig extends KrauseningConfig {

    /**
     * Whether the slack alerts are enabled.
     */
    @Key("teams.alert.enabled")
    @DefaultValue("false")
    Boolean teamsEnabled();
}
