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

import com.boozallen.aiops.mda.ManualActionNotificationService;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.generator.config.deployment.spark.SparkDependencyConfiguration;
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.generator.util.PythonGeneratorUtils;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

/**
 * Enables the generation of {@code pyproject.toml} configurations in the root project directory of Habushu modules.
 */
public abstract class AbstractPyProjectGenerator extends RootFileGenerator {
    protected ManualActionNotificationService manualActionNotificationService = new ManualActionNotificationService();

    @Override
    public void generate(GenerationContext generationContext) {
        VelocityContext vc = super.getNewVelocityContext(generationContext);

        String aissembleVersion = PipelineUtils.getAiSSEMBLEVersion(generationContext);
        SparkDependencyConfiguration config = SparkDependencyConfiguration.getInstance();
        vc.put(VelocityProperty.AISSEMBLE_VERSION, aissembleVersion);
        vc.put(VelocityProperty.AISSEMBLE_PYTHON_VERSION, PythonGeneratorUtils.getPythonDependencyVersion(aissembleVersion));
        vc.put(VelocityProperty.ARTIFACT_ID_PYTHON_CASE, PythonGeneratorUtils.normalizeToPythonCase(generationContext.getArtifactId()));
        vc.put(VelocityProperty.PROJECT_VERSION_PYTHON, PythonGeneratorUtils.getPythonDependencyVersion(generationContext.getVersion()));
        vc.put(VelocityProperty.PROJECT_NAME, generationContext.getRootArtifactId());
        vc.put("versionSpark", config.getSparkVersion());
        vc = PythonGeneratorUtils.populateCommonPythonContext(vc, generationContext);

        doGenerateFile(generationContext, vc);
    }

    /**
     * Handles invoking {@link #generateFile(GenerationContext, VelocityContext)} and provides an extension opportunity
     * for generator classes that might extend {@link TargetedPipelinePyProjectGenerator} to augment the default provided
     * {@link VelocityContext} population with additional pipeline-specific attributes or propagate notifications
     * to the user.
     *
     * @param generationContext Fermenter generation context.
     * @param velocityContext   pre-populated context that contains commonly used attributes.
     */
    protected void doGenerateFile(GenerationContext generationContext, VelocityContext velocityContext) {
        generateFile(generationContext, velocityContext);
    }
}
