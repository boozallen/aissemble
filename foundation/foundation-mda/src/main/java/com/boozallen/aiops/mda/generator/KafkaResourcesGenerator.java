package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
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
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.Step;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

public class KafkaResourcesGenerator extends ModelAgnosticResourcesGenerator {
    /*--~-~-~~
     * Usages:
     * | Target               | Template                          | Generated File                        |
     * |----------------------|-----------------------------------|---------------------------------------|
     * | messagingProperties  | metadata/messaging.properties.vm  | krausening/base/messaging.properties  |
     */


    private final String messagingType = "messaging";

    @Override
    public void generate(GenerationContext context) {
        Pipeline pipeline = PipelineUtils.getTargetedPipeline(context, metadataContext);
        boolean needsKafka = false;
        for(Step step : pipeline.getSteps()) {
            if((step.getInbound() != null && messagingType.equals(step.getInbound().getType()))
                    || (step.getOutbound() != null && messagingType.equals(step.getOutbound().getType()))) {
                needsKafka = true;
            }
        }

        if(needsKafka) {
            super.generate(context);
        } else {
            return;
        }
    }
}
