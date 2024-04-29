package com.boozallen.aissemble.data.lineage.consumer.subclass;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Consumer Base
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.data.lineage.consumer.MessageHandler;
import org.eclipse.microprofile.reactive.messaging.Message;

/**
 * Fake impl class that, when triggered to process a message, will throw an exception.
 */
public class ExceptionalMessageReceiver extends MessageHandler {
    protected void processRunEvent(Message<String> eventMessage) {
        int x = 1/0;
    }
}
