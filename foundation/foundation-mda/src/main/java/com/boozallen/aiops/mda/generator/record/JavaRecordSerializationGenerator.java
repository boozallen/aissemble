package com.boozallen.aiops.mda.generator.record;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
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

import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.Record;
import com.boozallen.aiops.mda.metamodel.element.StepDataRecordType;
import com.boozallen.aiops.mda.metamodel.element.java.JavaPipeline;
import com.boozallen.aiops.mda.metamodel.element.java.JavaStep;
import com.boozallen.aiops.mda.metamodel.element.java.JavaStepDataBinding;

import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.util.Map;

/**
 * Generates Serialization and Deserialization functionality for all custom record types that are
 * used as the record type for the outbound or inbound of a messaging step.
 */
public class JavaRecordSerializationGenerator extends JavaRecordGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                      | Template                                                     | Generated File                                     |
     * |-----------------------------|--------------------------------------------------------------|----------------------------------------------------|
     * | javaRecordDeserializerBase  | data-delivery-data-records/record.deserializer.base.java.vm  | ${basePackage}/${recordName}DeserializerBase.java  |
     * | javaRecordDeserializerImpl  | data-delivery-data-records/record.deserializer.impl.java.vm  | ${basePackage}/${recordName}Deserializer.java      |
     * | javaRecordSerializerBase    | data-delivery-data-records/record.serializer.base.java.vm    | ${basePackage}/${recordName}SerializerBase.java    |
     * | javaRecordSerializerImpl    | data-delivery-data-records/record.serializer.impl.java.vm    | ${basePackage}/${recordName}Serializer.java        |
     */

    @Override
    protected boolean shouldGenerateFile(GenerationContext context, Record currentRecord) {
        AissembleModelInstanceRepository metamodelRepository = (AissembleModelInstanceRepository) context.getModelInstanceRepository();

        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);
        return pipelineMap.values().stream()
                .map(JavaPipeline::new)
                .anyMatch(pipeline -> usesRecordWithMessaging(pipeline, currentRecord));
    }

    /**
     * Determines whether any steps in this pipeline use the given record for messaging.
     *
     * @param messagingPipeline the pipeline
     * @param currentRecord the record type
     * @return true if the record is used for messaging
     */
    private boolean usesRecordWithMessaging(JavaPipeline messagingPipeline, Record currentRecord) {
        for (JavaStep eachStep : messagingPipeline.getMessagingSteps()) {
            if (readsRecord(eachStep, currentRecord) || sendsRecord(eachStep, currentRecord)){
                return true;
            }
        }
        return false;
    }

    private boolean readsRecord(JavaStep step, Record currentRecord) {
        return step.hasMessagingInbound()
                && usesRecord(step.getInbound(), currentRecord);
    }

    private boolean sendsRecord(JavaStep step, Record currentRecord) {
        return step.hasMessagingOutbound()
                && usesRecord(step.getOutbound(), currentRecord);
    }

    private boolean usesRecord(JavaStepDataBinding dataBinding, Record currentRecord) {
        StepDataRecordType recordType = dataBinding.getRecordType();
        return recordType != null && recordType.getRecordType().equals(currentRecord);
    }
}
