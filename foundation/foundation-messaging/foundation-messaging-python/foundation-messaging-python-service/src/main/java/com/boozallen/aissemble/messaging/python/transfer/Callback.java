package com.boozallen.aissemble.messaging.python.transfer;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Messaging::Python::Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */


/**
 * Defines the interface for the python Callback class to implement.
 * Enables py4j to freely pass the Callback object between the client and service.
 */
public interface Callback {
    /**
     * Map to the python Callback object and allow Java to execute the callback function from python client
     * @param msgHandle the MessageHandle passed from the service
     */
    Object execute(MessageHandle msgHandle);
}
