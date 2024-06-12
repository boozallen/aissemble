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

import com.boozallen.aissemble.core.metadata.MetadataModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Deserializer for the MetadataModel for use in messaging
 */
public class MetadataModelDeserializer extends ObjectMapperDeserializer<MetadataModel> {
    public MetadataModelDeserializer() {
        super(MetadataModel.class);
    }

    /**
     * Deserialize input into a MetadataModel
     * @param topic the topic the message is pulled from
     * @param data the data representing a MetadataModel
     * @return the MetadataModel
     */
    @Override
    public MetadataModel deserialize(String topic, byte[] data) {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());

        if (data == null) {
            return null;
        }

        try (InputStream is = new ByteArrayInputStream(data)) {
            return mapper.readValue(is, MetadataModel.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
