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
import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import java.util.Map;

/**
 * Shared logic for all pipelines automatically generated to support automated tasks of aiSSEMBLE such as
 * data profiling and provenance
 */
public abstract class AbstractApplicationSupportModuleGenerator extends AbstractMavenModuleGenerator{
    protected void generateCommon(GenerationContext generationContext, String moduleArtifactId) {
        VelocityContext velocityContext = getNewVelocityContext(generationContext);

        String rootArtifactId = generationContext.getRootArtifactId();

        velocityContext.put(VelocityProperty.ROOT_ARTIFACT_ID, rootArtifactId);
        velocityContext.put(VelocityProperty.PARENT_ARTIFACT_ID, generationContext.getArtifactId());
        velocityContext.put(VelocityProperty.MODULE_ARTIFACT_ID, moduleArtifactId);
        velocityContext.put(VelocityProperty.GROUP_ID, generationContext.getGroupId());
        velocityContext.put(VelocityProperty.VERSION, generationContext.getVersion());
        velocityContext.put(VelocityProperty.PARENT_DESCRIPTIVE_NAME, generationContext.getDescriptiveName());
        velocityContext.put(VelocityProperty.BASE_PACKAGE, generationContext.getBasePackage());

        String baseFileName = generationContext.getOutputFile();
        String fileName = replace(VelocityProperty.MODULE_ARTIFACT_ID, baseFileName, moduleArtifactId);
        generationContext.setOutputFile(fileName);

        generateFile(generationContext, velocityContext);
    }

    /**
     * Returns true if at least one pipeline is configured with a step that has
     * data profiling enabled.
     * @return
     */
    protected boolean isDataProfilingEnabled() {
        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);
        return Pipeline.aPipelineExistsWhere(pipelineMap.values(), Pipeline::isDataProfilingEnabled);
    }

    /**
     * Returns the artifact id for the module to generate.
     *
     * @param context
     *            generation context
     * @return artifact id for the module to generate
     */
    protected String getModuleArtifactId(GenerationContext context, String appName) {
        String parentArtifactId = context.getArtifactId();
        return parentArtifactId.replace("pipelines", appName);
    }
}
