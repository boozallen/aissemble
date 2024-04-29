package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.Step;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.util.Objects;

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
