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

import java.util.UUID;

import com.boozallen.aissemble.alerting.core.Alert.Status;

/**
 * Interface for an alert producer.
 */
public interface AlertProducerApi {

    /**
     * Publishes an alert.
     * 
     * @param status
     *            the status of the alert to publish
     * @param message
     *            the message of the alert to publish
     * @return the id of the published alert
     */
    public UUID sendAlert(Status status, String message);

}
