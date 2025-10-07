package com.boozallen.aissemble.data.lineage.transport;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Java
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
