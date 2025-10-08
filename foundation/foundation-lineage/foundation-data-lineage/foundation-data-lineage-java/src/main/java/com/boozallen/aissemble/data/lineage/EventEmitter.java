package com.boozallen.aissemble.data.lineage;

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

import com.boozallen.aissemble.data.lineage.config.ConfigUtil;
import com.boozallen.aissemble.data.lineage.transport.MessagingTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.enterprise.inject.spi.CDI;

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
