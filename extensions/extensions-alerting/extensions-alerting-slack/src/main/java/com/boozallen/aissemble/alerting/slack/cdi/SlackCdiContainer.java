package com.boozallen.aissemble.alerting.slack.cdi;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Alerting::Slack
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.alerting.core.cdi.AlertingCdiContainer;

/**
 * {@link SlackCdiContainer} contains the CDI components needed to use Slack
 * Alerting.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class SlackCdiContainer extends AlertingCdiContainer {

    public SlackCdiContainer() {
        super();
        create(new SlackCdiContext());
    }

}
