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
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import com.boozallen.aiops.mda.metamodel.element.BasePipelineDecorator;
import com.boozallen.aiops.mda.metamodel.element.BaseStepDecorator;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;
import org.apache.maven.model.Model;

import java.nio.file.Paths;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataLineageResourcesGenerator extends AbstractResourcesGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                   | Template                                     | Generated File                           |
     * |--------------------------|----------------------------------------------|------------------------------------------|
     * | dataLineageProperties    | general-docker/data.lineage.properties.vm    | krausening/base/data-lineage.properties  |
     * | mlflowLineageProperties  | general-docker/mlflow.lineage.properties.vm  | krausening/base/data-lineage.properties  |
     */

    private static final Logger logger = LoggerFactory.getLogger(DataLineageResourcesGenerator.class);

    @Override
    public void generate(GenerationContext generationContext) {
        VelocityContext vc = getNewVelocityContext(generationContext);
        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);
        List<Pipeline> pipelines = new ArrayList<>(pipelineMap.values());
        Map<String, List<String>> lineageStepsByPipeline = new HashMap<>();
        boolean lineageEnabled = false;

        for (Pipeline pipeline : pipelines) {
            BasePipelineDecorator decorated = new BasePipelineDecorator(pipeline);
            if (pipeline.getDataLineage() || decorated.isModelLineageSupportNeeded()) {
                lineageEnabled = true;
                ArrayList<String> lineageSteps = new ArrayList<>();
                for (BaseStepDecorator step : decorated.getSteps()) {
                    // Data Lineage is enabled at the pipeline level, while Model Lineage is enabled at the step level
                    if (decorated.getDataLineage() || step.isModelLineageEnabled()) {
                        lineageSteps.add(PipelineUtils.getQualifiedPipelineStepName(pipeline, step));
                    }
                }
                lineageStepsByPipeline.putIfAbsent(pipeline.getName(), lineageSteps);
            }
        }

        /**
         * If we use the context's scm url property, it will give us the value of the current module. We want
         * the scm url from the project root, not the docker module where this properties file will be written.
         */
        MavenProject project = readPom(generationContext.getExecutionRootDirectory());
        vc.put(VelocityProperty.SCM_URL, project.getScm().getUrl());

        if (lineageEnabled) {
            vc.put(VelocityProperty.DATA_LINEAGE_STEPS_BY_PIPELINE, lineageStepsByPipeline);
            String fileName = generationContext.getOutputFile();
            generationContext.setOutputFile(fileName);
            generateFile(generationContext, vc);
        }
    }

    private MavenProject readPom(File rootDirectory) {
        File pomFile = Paths.get(rootDirectory.getAbsolutePath(), "pom.xml").toFile();
        Model model = null;
        FileReader reader = null;
        MavenXpp3Reader mavenreader = new MavenXpp3Reader();
        try {
            reader = new FileReader(pomFile);
            model = mavenreader.read(reader);
            model.setPomFile(pomFile);
        }catch(Exception ex){
            logger.error("Encountered error parsing root pom file: " + ex.getMessage());
            throw new RuntimeException(ex.getCause());
        }
        return new MavenProject(model);
    }
}
