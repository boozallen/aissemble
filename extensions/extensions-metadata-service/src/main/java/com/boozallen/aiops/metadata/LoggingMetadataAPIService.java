package com.boozallen.aiops.metadata;

/*-
 * #%L
 * AIOps Docker Baseline::AIOps Metadata Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.core.metadata.MetadataAPI;
import com.boozallen.aissemble.core.metadata.MetadataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to handle basic logging of metadata. Intended for testing purposes and not suited for production.
 */
@ApplicationScoped
@MetadataAPIType("logging")
public class LoggingMetadataAPIService implements MetadataAPI {

    Logger logger = LoggerFactory.getLogger(LoggingMetadataAPIService.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void createMetadata(MetadataModel metadata) {
        logger.warn("Metadata being handled by default Logging implementation. This is designed " +
                "for testing and is not suited for production use-cases.");
        if (metadata != null) {
            StringBuilder builder = new StringBuilder();
            builder
                    .append("Metadata:")
                    .append("\nSubject: ")
                    .append(metadata.getSubject())
                    .append("\nResource: ")
                    .append(metadata.getResource())
                    .append("\nAction: ")
                    .append(metadata.getAction())
                    .append("\nTimestamp: ")
                    .append(metadata.getTimestamp().toString())
                    .append("\nAdditional Properties: ");

            metadata.getAdditionalValues()
                    .forEach((key, value) -> {
                        builder.append("\n\t")
                                .append(key)
                                .append(": ")
                                .append(value);
                    });

            logger.info(builder.toString());
        } else {
            logger.warn("Attempting to create null metadata.");
        }
    }

    @Override
    public List<MetadataModel> getMetadata(Map<String, Object> searchParams) {
        return new ArrayList<>();
    }
}

