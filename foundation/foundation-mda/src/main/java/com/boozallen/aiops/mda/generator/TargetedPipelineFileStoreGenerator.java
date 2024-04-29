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

import com.boozallen.aiops.mda.generator.common.AbstractGeneratorAissemble;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.metamodel.element.BaseFileStoreDecorator;
import com.boozallen.aiops.mda.metamodel.element.BasePipelineDecorator;
import com.boozallen.aiops.mda.metamodel.element.FileStore;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

/**
 * Iterates through each pipeline in the metamodel and enables the generation of a single file for each pipeline.
 */
public class TargetedPipelineFileStoreGenerator extends AbstractGeneratorAissemble {
    /*--~-~-~~
     * Usages:
     * | Target             | Template                            | Generated File                                  |
     * |--------------------|-------------------------------------|-------------------------------------------------|
     * | javaFileStoreBase  | file-store/file.store.base.java.vm  | java/${basePackage}/filestore/${name}Base.java  |
     * | javaFileStoreImpl  | file-store/file.store.impl.java.vm  | java/${basePackage}/filestore/${name}.java      |
     */


    /**
     * {@inheritDoc}
     */
    @Override
    public void generate(GenerationContext generationContext) {
        String baseOutputFile = generationContext.getOutputFile();

        Pipeline pipeline = PipelineUtils.getTargetedPipeline(generationContext, metadataContext);
        BasePipelineDecorator targetPipeline = new BasePipelineDecorator(pipeline);
        targetPipeline.validate();

        for (FileStore fileStore : targetPipeline.getFileStores()) {
            BaseFileStoreDecorator decoratedStore = new BaseFileStoreDecorator(fileStore);

            VelocityContext vc = getNewVelocityContext(generationContext);
            vc.put(VelocityProperty.BASE_PACKAGE, generationContext.getBasePackage());
            vc.put(VelocityProperty.PIPELINE, targetPipeline);
            vc.put(VelocityProperty.FILE_STORE, decoratedStore);

            String fileName = replace("name", baseOutputFile, decoratedStore.getFullName());
            fileName = replace("basePackage", fileName, generationContext.getBasePackageAsPath());
            generationContext.setOutputFile(fileName);

            generateFile(generationContext, vc);
        }
    }

    @Override
    protected String getOutputSubFolder() {
        return "";
    }
}
