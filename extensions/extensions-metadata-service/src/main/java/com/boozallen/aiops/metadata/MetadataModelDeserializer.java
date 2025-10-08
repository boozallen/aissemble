package com.boozallen.aiops.metadata;

/*-
 * #%L
 * AIOps Docker Baseline::AIOps Metadata Service
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
