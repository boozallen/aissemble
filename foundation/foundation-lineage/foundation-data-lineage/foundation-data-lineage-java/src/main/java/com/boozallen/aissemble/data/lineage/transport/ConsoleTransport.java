package com.boozallen.aissemble.data.lineage.transport;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.common.Constants;
import io.smallrye.reactive.messaging.annotations.Merge;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * If added to the CDI container, this class will log all emitted Run Events to the console.  Adding this class will
 * not negate any other behavior or receivers.
 */
@ApplicationScoped
public class ConsoleTransport {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleTransport.class);

    /**
     * Receives incoming RunEvents to be published to the console.
     * @param event String representation of the Run Event to log.
     */
    @Merge
    @Incoming(Constants.DATA_LINEAGE_CHANNEL_NAME)
    public void emit(String event) {
        // We extract handling of the message in order to enable overriding.  In our case, this is for testing purposes.
        handleReceivedMessage(event);
    }

    protected void handleReceivedMessage(String event) {
        logger.info(event);
    }
}
