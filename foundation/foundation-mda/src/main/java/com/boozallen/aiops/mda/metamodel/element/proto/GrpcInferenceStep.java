package com.boozallen.aiops.mda.metamodel.element.proto;

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

import com.boozallen.aiops.mda.metamodel.element.BaseStepDecorator;
import com.boozallen.aiops.mda.metamodel.element.Step;

/**
 * Models an inference step within a pipeline, specifically facilitating the generation
 * of gRPC services and associated Protobuf messages utilized to expose the corresponding
 * inference capabilities via an API
 */
public class GrpcInferenceStep extends BaseStepDecorator {

    /**
     * New decorator for {@link Step}.
     *
     * @param stepToDecorate instance to decorate
     */
    public GrpcInferenceStep(Step stepToDecorate) {
        super(stepToDecorate);
    }

    /**
     * If the inference step is configured to receive a {@link com.boozallen.aiops.mda.metamodel.element.Record}
     * as input, return it wrapped as a {@link ProtobufRecord}.
     *
     * @return
     */
    public ProtobufRecord getInboundRecord() {
        return hasInboundRecordType() ? new ProtobufRecord(getInbound().getRecordType().getRecordType()) : null;
    }

    /**
     * If the inference step is configured to expose a {@link com.boozallen.aiops.mda.metamodel.element.Record}
     * as output, return it wrapped as a {@link ProtobufRecord}.
     *
     * @return
     */
    public ProtobufRecord getOutboundRecord() {
        return hasOutboundRecordType() ? new ProtobufRecord(getOutbound().getRecordType().getRecordType()) : null;
    }
}
