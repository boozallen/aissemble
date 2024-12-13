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

import com.boozallen.aiops.mda.generator.common.PipelineEnum;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import com.boozallen.aiops.mda.metamodel.element.BasePipelineDecorator;
import com.boozallen.aiops.mda.metamodel.element.BaseStepDecorator;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.apache.maven.model.Model;

import java.nio.file.Paths;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractLineageResourcesGenerator extends AbstractResourcesGenerator {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLineageResourcesGenerator.class);

    public void setPipelineType(PipelineEnum pipelineEnum) {;
        pipelineType = pipelineEnum;
    }

    private PipelineEnum pipelineType;

    @Override
    public void generate(GenerationContext generationContext) {
        VelocityContext vc = getNewVelocityContext(generationContext);
        AissembleModelInstanceRepository metamodelRepository = (AissembleModelInstanceRepository) generationContext.getModelInstanceRepository();

        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);
        List<Pipeline> pipelines = new ArrayList<>(pipelineMap.values());
        Map<String, List<String>> lineageStepsByPipeline = new HashMap<>();
        boolean lineageEnabled = false;
        for (Pipeline pipeline : pipelines) {
            BasePipelineDecorator decorated = new BasePipelineDecorator(pipeline);
            // Separate the machine-learning and data-flow pipeline properties since the properties are used in different docker container
            if (pipeline.getType().getName().equals(PipelineEnum.getPipelineType(pipelineType)) &&
                    (pipeline.getDataLineage() || decorated.isModelLineageSupportNeeded())) {
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
