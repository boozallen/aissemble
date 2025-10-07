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
    protected static final String DOCKER_PROJECT_REPOSITORY_URL = "dockerProjectRepositoryUrl";

    @Override
    public void generate(GenerationContext context) {
        String rootModuleName = context.getRootArtifactId();
        String dockerProjectRepositoryUrl = context.getPropertyVariables().get(DOCKER_PROJECT_REPOSITORY_URL);

        if (rootModuleName == null || rootModuleName.isEmpty()) {
            logger.error("Root module could not be determined!");
        } else {
            String inferenceModule = context.getArtifactId()
                    .replace(rootModuleName + "-", "")
                    .replace("-docker", "");

            VelocityContext vc = getNewVelocityContext(context);
            vc.put(VelocityProperty.INFERENCE_MODULE, inferenceModule);
            vc.put(VelocityProperty.INFERENCE_MODULE_SNAKE_CASE, PythonGeneratorUtils.normalizeToPythonCase(inferenceModule));
            vc.put(VelocityProperty.DOCKER_PROJECT_REPOSITORY_URL, dockerProjectRepositoryUrl);

            generateFile(context, vc);
        }
    }
}
