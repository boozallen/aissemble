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

import com.boozallen.aissemble.data.lineage.config.ConfigUtil;
import com.boozallen.aissemble.common.Constants;
import io.openlineage.client.OpenLineage;
import io.openlineage.client.OpenLineageClientUtils;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Emits lineage events as messages as configured via smallrye.
 */
@ApplicationScoped
public class MessagingTransport {
    private static ConfigUtil util = ConfigUtil.getInstance();

    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 20)
    @Inject
    @Broadcast
    @Channel(Constants.DATA_LINEAGE_CHANNEL_NAME)
    Emitter<String> emitter;

    public void emit(OpenLineage.RunEvent evt) {
        if ("True".equalsIgnoreCase(util.shouldEmitOverMessaging())) {
            emitter.send(OpenLineageClientUtils.toJson(evt));
        }
    }
}
