package com.boozallen.aissemble.alerting.core.cdi;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Alerting::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.messaging.core.cdi.MessagingCdiContainer;

public class AlertingCdiContainer extends MessagingCdiContainer {

    /**
     * Constructor for a cdi container configured for alerting.
     */
    public AlertingCdiContainer() {
        super();
        create(new AlertingCdiContext());
    }

}
