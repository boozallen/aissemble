package com.boozallen.aissemble.core.metadata.producer;

/*-
 * #%L
 * aiSSEMBLE Core::Metadata::aiSSEMBLE Metadata Producer
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.core.metadata.MetadataModel;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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
