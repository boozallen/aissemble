package com.boozallen.aissemble.alerting.core.consumer;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Alerting::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aissemble.alerting.core.Alert;
import com.boozallen.aissemble.alerting.core.Alert.Status;

@ApplicationScoped
public class AlertLogger {

    private static final Logger logger = LoggerFactory.getLogger(AlertLogger.class);

    @Incoming("alerts")
    public void logAlert(Alert alert) {
        if (Status.FAILURE.equals(alert.getStatus())) {
            logger.warn(alert.getMessage());
        } else {
            logger.info(alert.getMessage());
        }
    }

}
