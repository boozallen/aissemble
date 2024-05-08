package com.boozallen.aissemble.data.lineage;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.data.lineage.config.ConfigUtil;
import com.boozallen.aissemble.data.lineage.transport.MessagingTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.enterprise.inject.spi.CDI;

public final class EventEmitter {

    private static final Logger logger = LoggerFactory.getLogger(EventEmitter.class);
    private static ConfigUtil util = ConfigUtil.getInstance();

    private EventEmitter() {}

    /**
     * Sends a String-serialized RunEvent over the mechanism specified in `microprofile-config.properties`.
     * @param runEvent The RunEvent to send out.
     */
    public static void emitEvent(RunEvent runEvent) {
        if ("true".equalsIgnoreCase(util.getDataLineageEnabled())) {
            logger.debug("Recording data lineage data...");
            CDI.current().select(MessagingTransport.class).get().emit(runEvent.getOpenLineageRunEvent());
            logger.debug("Data lineage recorded");
        } else {
            logger.debug("Data lineage is disabled!  Bypassing emission--");
        }
    }
}
