package com.boozallen.aissemble.alerting;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Alerting::Teams
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.boozallen.aissemble.alerting.teams.TeamsMessageService;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TeamsAlertingTest {
    private boolean success;

    @Inject
    TeamsMessageService teamsMessageService;

    @Test
    public void testRun() {
        anAlertIsSentToTeams();
        theAlertIsSentToTheConfiguredTeamsChannelSuccessfully();
    }

    public void anAlertIsSentToTeams() {
        success = teamsMessageService.sendMessage("Test List\r- Test Item 1\r- Test Item 2: ✅");
    }

    public void theAlertIsSentToTheConfiguredTeamsChannelSuccessfully() {
        Assertions.assertTrue(success, "Message not sent successfully");
    }
}
