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
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.io.File;

/**
 * Generates a file in the root directory of a module.
 */
public class RootFileGenerator extends AbstractGeneratorAissemble {
    /*--~-~-~~
     * Usages:
     * | Target             | Template                               | Generated File                  |
     * |--------------------|----------------------------------------|---------------------------------|
     * | jenkinsBuildJob    | devops/jenkins-build-job.xml.vm        | devops/jenkins-build-job.xml    |
     * | jenkinsReleaseJob  | devops/jenkins-release-job.xml.vm      | devops/jenkins-release-job.xml  |
     * | airflowREADMEFile  | general-docker/airflow.readme.file.vm  | README.md                       |
     * | gitignoreFile      | gitignore.vm                           | .gitignore                      |
     * | tiltignoreFile     | tiltignore.vm                          | .tiltignore                     |
     */


    @Override
    public void generate(GenerationContext generationContext) {
        VelocityContext vc = super.getNewVelocityContext(generationContext);
        vc.put(VelocityProperty.PROJECT_GIT_URL, generationContext.getScmUrl());

        generateFile(generationContext, vc);
    }

    @Override
    protected File getBaseFile(GenerationContext generationContext) {
        return generationContext.getProjectDirectory();
    }

    @Override
    protected String getOutputSubFolder() {
        return "";
    }

}
