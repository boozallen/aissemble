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

import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aiops.mda.generator.util.PythonGeneratorUtils;

/**
 * Generates inference docker resources if any machine-learning pipelines with an inference step exist.
 */
public class InferenceDockerResourcesGenerator extends AbstractResourcesGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                  | Template                                 | Generated File            |
     * |-------------------------|------------------------------------------|---------------------------|
     * | inferenceGitkeepFile    | general-docker/gitkeep.vm                | krausening/base/.gitkeep  |
     * | inferenceDockerFile     | general-docker/inference.docker.file.vm  | docker/Dockerfile         |
     * | inferencePerceptorApp   | inference/inference.perceptor.app.py.vm  | perceptor/app.py          |
     * | inferencePerceptorYaml  | inference/inference.perceptor.yaml.vm    | perceptor/perceptor.yaml  |
     */


    private static final Logger logger = LoggerFactory.getLogger(InferenceDockerResourcesGenerator.class);

    @Override
    public void generate(GenerationContext context) {
        String rootModuleName = context.getRootArtifactId();

        if (rootModuleName == null || rootModuleName.isEmpty()) {
            logger.error("Root module could not be determined!");
        } else {
            String inferenceModule = context.getArtifactId()
                    .replace(rootModuleName + "-", "")
                    .replace("-docker", "");

            VelocityContext vc = getNewVelocityContext(context);
            vc.put(VelocityProperty.INFERENCE_MODULE, inferenceModule);
            vc.put(VelocityProperty.INFERENCE_MODULE_SNAKE_CASE, PythonGeneratorUtils.normalizeToPythonCase(inferenceModule));

            generateFile(context, vc);
        }
    }
}
