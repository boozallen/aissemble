package com.boozallen.aissemble.alerting.teams.cdi;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Alerting::Teams
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.alerting.core.cdi.AlertingCdiContainer;
import com.boozallen.aissemble.quarkus.context.QuarkusCdiContext;

/**
 * {@link TeamsCdiContainer} creates the cdi configurations needed to use the
 * Teams Alerts.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class TeamsCdiContainer extends AlertingCdiContainer {

    public TeamsCdiContainer() {
        super();
        create(new TeamsCdiContext());
        create(new QuarkusCdiContext());
    }

}
