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
import com.boozallen.aiops.mda.generator.util.MavenUtil;
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.generator.util.SemanticDataUtil;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.Step;
import com.boozallen.aiops.mda.metamodel.element.python.PythonPipeline;
import com.boozallen.aiops.mda.metamodel.element.python.PythonStep;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;

/**
 * Generates Python source modules for each step of the pipeline specified by
 * the {@code targetedPipeline} property in the {@code fermenter-mda} plugin
 * configuration.
 */
public abstract class TargetedPipelineStepPythonGenerator extends AbstractPythonGenerator {
    //--- ALERT: This generator is probably not used by any targets in targets.json

    private static final Logger logger = LoggerFactory.getLogger(TargetedPipelineStepPythonGenerator.class);
    private static final String AISSEMBLE_DATA_RECORDS_SEPARATE_MODULE_PROFILE = "aissemble-data-records-separate-module";

    @Override
    public void generate(GenerationContext generationContext) {
        String baseOutputFile = generationContext.getOutputFile();
        Pipeline pipeline = PipelineUtils.getTargetedPipeline(generationContext, metadataContext);
        PythonPipeline pythonTargetPipeline = new PythonPipeline(pipeline);
        pythonTargetPipeline.validate();

        for (Step step : pythonTargetPipeline.getSteps()) {
            if (shouldGenerateStep(step, generationContext)) {
                VelocityContext vc = getNewVelocityContext(generationContext);
                PythonStep pythonStep = new PythonStep(step);
                pythonStep.validate();
                pythonStep.setRecordGenerationInPipelineModule(MavenUtil.isRecordGenerationInPipelineModule(generationContext));
                pythonStep.setPythonDataRecordPackage(getPythonDataRecordPackage(generationContext));
                vc.put(VelocityProperty.STEP, pythonStep);
                vc.put(VelocityProperty.PIPELINE, pythonTargetPipeline);

                String fileName = replace("name", baseOutputFile, pythonStep.getLowercaseSnakeCaseName());
                generationContext.setOutputFile(fileName);

                generateFile(generationContext, vc);
            }
        }
    }

    /**
     * Provides an optional opportunity to short circuit generation for steps. This is often useful if extending to
     * target specific steps to specific templates.
     *
     * @param step
     *            step being considered
     * @param generationContext
     *            context of generation
     * @return whether or not to generate
     */
    protected boolean shouldGenerateStep(Step step, GenerationContext generationContext) {
        return true;
    }

    /**
     * Get the appropriate data record package from the given context
     * @param context the generation context
     * @return data record package name
     */
    private String getPythonDataRecordPackage(GenerationContext context) {
        File rootDir = context.getExecutionRootDirectory();
        String sharedDir = rootDir.getPath() + File.separator + MavenUtil.getSharedModuleName(rootDir);

        try {
            NodeList executions =  MavenUtil.getPomFileElements(new File(sharedDir),"execution");
            Node configuration = null;
            for (int i = 0; i < executions.getLength(); i++){
                Node execution = executions.item(i);
                if (execution.getNodeType() == Node.ELEMENT_NODE){
                    configuration = getDataRecordConfiguration(execution);
                    if (configuration != null) {
                        break;
                    }
                }
            }
            // check if the `generate-date-record` profile is `aissemble-data-records-separate-module`
            if (configuration != null && configuration.getTextContent().contains(AISSEMBLE_DATA_RECORDS_SEPARATE_MODULE_PROFILE)) {
                return MavenUtil.getDataRecordModuleName(context, metadataContext, MavenUtil.Language.PYTHON, SemanticDataUtil.DataRecordModule.CORE);
            }
        } catch (Exception e){
            logger.warn(String.format("Failed reading execution from pom file at %s; setting data record module to combined mode.", sharedDir));
        }
        return MavenUtil.getDataRecordModuleName(context, metadataContext, MavenUtil.Language.PYTHON, SemanticDataUtil.DataRecordModule.COMBINED);
    }

    /**
     * Get the data record configuration from given execution if it's the `generate-data-records` execution
     * @param execution execution node
     * @return data record configuration
     */
    private Node getDataRecordConfiguration(Node execution) {
        NodeList children = execution.getChildNodes();
        Node configuration = null;
        boolean isDataProfileExecution = false;
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equals("id") && child.getTextContent().equals("generate-data-records") && child.getNodeType() == Node.ELEMENT_NODE) {
                isDataProfileExecution = true;
            }
            if (child.getNodeName().equals("configuration")) {
                configuration = child;
            }
        }
        return isDataProfileExecution? configuration: null;
    }
}
