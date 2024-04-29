package com.boozallen.aissemble.alerting.core;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Alerting::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AlertCounter {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertCounter.class);
    
    private static final List<Alert> alerts = new ArrayList<>();
    
    @Incoming("alerts")
    public void countAlerts(Alert alert) {

        // add to list of alerts received for testing purposes
        alerts.add(alert);
        logger.info("{} total alerts received so far", alerts.size());
    }
    
    public static List<Alert> getAllAlerts(){
        return alerts;
    }

}
