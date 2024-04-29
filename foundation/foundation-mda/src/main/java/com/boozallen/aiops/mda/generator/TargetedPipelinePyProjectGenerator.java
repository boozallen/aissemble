package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.generator.util.SemanticDataUtil;
import com.boozallen.aiops.mda.metamodel.element.BasePipelineDecorator;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

/**
 * A {@link AbstractPyProjectGenerator} that enables the generation of {@code pyproject.toml} for the pipeline specified
 * by the {@code targetedPipeline} property that is provided via the {@code fermenter-mda} plugin configuration.
 */
public abstract class TargetedPipelinePyProjectGenerator extends AbstractPyProjectGenerator {

    @Override
    public void doGenerateFile(GenerationContext generationContext, VelocityContext velocityContext) {
        Pipeline pipeline = PipelineUtils.getTargetedPipeline(generationContext, metadataContext);
        velocityContext.put(VelocityProperty.PIPELINE, new BasePipelineDecorator(pipeline));
        velocityContext.put(VelocityProperty.ENABLE_SEMANTIC_DATA_SUPPORT, SemanticDataUtil.hasSemanticDataByContext(metadataContext));
        doGenerateFile(generationContext, velocityContext, pipeline);
    }

    /**
     * Handles invoking {@link #generateFile(GenerationContext, VelocityContext)} and provides an extension opportunity
     * for generator classes that might extend {@link TargetedPipelinePyProjectGenerator} to augment the default provided
     * {@link VelocityContext} population with additional pipeline-specific attributes or propagate notifications
     * to the user.
     *
     * @param generationContext Fermenter generation context.
     * @param velocityContext   pre-populated context that contains commonly used attributes.
     * @param pipeline          targeted pipeline for which this generator is being applied.
     */
    protected void doGenerateFile(GenerationContext generationContext, VelocityContext velocityContext,
                                  Pipeline pipeline) {
        generateFile(generationContext, velocityContext);
    }
}
