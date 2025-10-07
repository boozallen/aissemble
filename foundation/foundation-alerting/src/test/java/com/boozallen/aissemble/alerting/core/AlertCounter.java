package com.boozallen.aissemble.alerting.core;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Alerting::Core
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
