package com.boozallen.aiops.core.metadata.producer;

/*-
 * #%L
 * AIOps Core::Metadata::AIOps Metadata Producer
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.core.metadata.MetadataModel;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Metadata producer
 */
@ApplicationScoped
public class MetadataProducer {

    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 20)
    @Inject
    @Channel("metadata-ingest")
    protected Emitter<MetadataModel> metadataEmitter;

    /**
     * Send metadata to be ingested
     * @param model the metadata model
     */
    public void send(final MetadataModel model) {
        metadataEmitter.send(model);
    }
}
