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

import com.boozallen.aissemble.data.lineage.config.ConfigUtil;
import com.boozallen.aissemble.common.Constants;
import io.openlineage.client.OpenLineage;
import io.openlineage.client.OpenLineageClientUtils;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
