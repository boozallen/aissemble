package com.boozallen.aissemble.alerting.core;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Alerting::Core
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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AlertSerializer for use in messaging
 *
 */

public class AlertSerializer implements Serializer<Alert> {
    private static final Logger logger = LoggerFactory.getLogger(AlertSerializer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, Alert alert) {
        try {
            if (alert == null){
                logger.warn("No Alert received for serialization");
                return null;
            }

            return objectMapper.writeValueAsBytes(alert);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing Alert to byte[]");
        }
    }
}
