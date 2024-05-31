package com.boozallen.aiops.metadata;

/*-
 * #%L
 * AIOps Metadata Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.core.metadata.MetadataAPI;
import com.boozallen.aissemble.core.metadata.MetadataModel;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Ingest Metadata from messaging
 */
@ApplicationScoped
public class MetadataIngest {

    @Inject
    @MetadataAPIType("hive")
    MetadataAPI metadataAPI;

    /**
     * Ingest a metadata model from the topic and save.
     * @param model the metadata model to save
     */
    @Incoming("metadata-ingest")
    public void ingest(final MetadataModel model) {
        metadataAPI.createMetadata(model);
    }
}
