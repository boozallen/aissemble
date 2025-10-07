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
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * AlertDeserializer for use in messaging
 *
 */

public class AlertDeserializer implements Deserializer<Alert> {
    private static final Logger logger = LoggerFactory.getLogger(AlertDeserializer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Alert deserialize(String topic, byte[] alert) {
        try {
            if (alert == null){
                logger.warn("No Alert received for deserialization");
                return null;
            }
            return objectMapper.readValue(new String(alert, StandardCharsets.UTF_8), Alert.class);
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing byte[] to Alert");
        }
    }
}
