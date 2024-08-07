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
import org.apache.maven.plugin.logging.Log;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.AbstractModelAgnosticGenerator;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.generator.GenerationException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class PipelineModelGenerator extends AbstractModelAgnosticGenerator {
    /*--~-~-~~
     * Usages:
     * | Target         | Template                                | Generated File    |
     * |----------------|-----------------------------------------|-------------------|
     * | pipelineModel  | pipeline-models/pipeline-model-file.vm  | ${pipeline}.json  |
     */


    protected String pipelineName = "pipelineName";
    protected String pipelineTypeName = "pipelineTypeName";
    protected String implementationName = "implementationName";
    protected String filePath = "${project.basedir}/${project.artifactId}-pipeline-models/src/main/resources/pipelines/";

    @Override
    protected String getOutputSubFolder() {
        return "resources/pipelines/";
    }

    @Override
    public void generate(GenerationContext context) {
        VelocityContext vc = getNewVelocityContext(context);
        vc.put(VelocityProperty.BASE_PACKAGE, context.getBasePackage());

        Map<String, String> properties = context.getPropertyVariables();
        vc.put(pipelineName, properties.get(pipelineName));
        vc.put(pipelineTypeName, properties.get(pipelineTypeName));
        vc.put(implementationName, properties.get(implementationName));

        String fileName = replace(VelocityProperty.PIPELINE, context.getOutputFile(), properties.get("pipelineName"));
        filePath = replace("project.basedir", filePath, properties.get("baseDir"));
        filePath = replace("project.artifactId", filePath, properties.get("baseArtifactName"));

        Template template = context.getEngine().getTemplate(context.getTemplateName());
        File outputFile = new File(filePath + fileName);

        outputFile.getParentFile().mkdirs();

        try (Writer fw = new FileWriter(outputFile, true)) {
            template.merge(vc, fw);
        } catch (IOException e) {
            throw new GenerationException("Failed to add new module to the project!", e);
        }
    }
}
