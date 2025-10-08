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

import org.apache.commons.io.FileUtils;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.generator.GenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Temporary migration routine that will be replaced with a formal capability in the aissemble-maven-plugin.
 */
public class JteMigrationRootFileGenerator extends RootFileGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                 | Template                                 | Generated File                    |
     * |------------------------|------------------------------------------|-----------------------------------|
     * | jteJenkinsfileBuild    | devops/jte.JenkinsfileBuild.groovy.vm    | devops/JenkinsfileBuild.groovy    |
     * | jteJenkinsfileRelease  | devops/jte.JenkinsfileRelease.groovy.vm  | devops/JenkinsfileRelease.groovy  |
     * | jteConfiguration       | devops/jte.PipelineConfig.groovy.vm      | devops/PipelineConfig.groovy      |
     */


    private static final Logger logger = LoggerFactory.getLogger(JteMigrationRootFileGenerator.class);

    @Override
    public void generate(GenerationContext generationContext) {
        // first, check for jte-specific migration needs and perform them, if appropriate:
        migrateExistingJteFiles(generationContext);

        super.generate(generationContext);
    }

    private void migrateExistingJteFiles(GenerationContext generationContext) {
        File baseJteDirectory = new File(getBaseFile(generationContext), "/jte");
        if (baseJteDirectory.exists()) {
            logger.info("");
            logger.info("MIGRATE: ***************************************************************");
            logger.info("MIGRATE: Found existing resources in jte/ - migrating them to devops/...");
            File targetDevopsDirectory = new File(getBaseFile(generationContext), "/devops");
            try {
                int fileCount = baseJteDirectory.listFiles().length;
                FileUtils.copyDirectory(baseJteDirectory, targetDevopsDirectory);
                renameFile(targetDevopsDirectory, "Jenkinsfile", "JenkinsfileBuild.groovy");
                renameFile(targetDevopsDirectory, "pipeline_config.groovy", "PipelineConfig.groovy");
                FileUtils.deleteDirectory(baseJteDirectory);
                logger.info("MIGRATE: Successfully migrated {} files from jte/ to devops/", fileCount);
                logger.info("MIGRATE: ***************************************************************");
                logger.info("");

            } catch (IOException e) {
                throw new GenerationException("Could not migrate existing jte files!", e);
            }
        }
    }

    private void renameFile(File targetDevopsDirectory, String currentFile, String newFile) {
        File legacyFile = new File(targetDevopsDirectory, currentFile);
        if (legacyFile.exists()) {
            File renamedFile = new File(targetDevopsDirectory, newFile);
            legacyFile.renameTo(renamedFile);
            logger.info("MIGRATE: renamed {} --> {}", currentFile, newFile);
        }
    }

}
