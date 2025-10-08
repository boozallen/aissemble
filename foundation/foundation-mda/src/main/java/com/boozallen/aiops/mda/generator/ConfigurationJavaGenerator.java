package com.boozallen.aiops.mda.generator;

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

import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import com.boozallen.aiops.mda.metamodel.element.java.JavaPipeline;

/**
 * Provides access to all model constructs but does not iterate through any to
 * generate. This is useful for generating single files like, like those often
 * needed for configuration.
 */
public class ConfigurationJavaGenerator extends AbstractJavaGenerator {
    /*--~-~-~~
     * Usages:
     * | Target               | Template                       | Generated File                               |
     * |----------------------|--------------------------------|----------------------------------------------|
     * | cdiContainerFactory  | cdi.container.factory.java.vm  | ${basePackage}/cdi/CdiContainerFactory.java  |
     */


    /**
     * {@inheritDoc}
     */
    @Override
    public void generate(GenerationContext generationContext) {
        super.generate(generationContext);
        JavaPipeline targetedPipeline = new JavaPipeline(PipelineUtils.getTargetedPipeline(generationContext, metadataContext));

        VelocityContext vc = getNewVelocityContext(generationContext);
        vc.put(VelocityProperty.PIPELINE, targetedPipeline);

        generateFile(generationContext, vc);
    }

}
