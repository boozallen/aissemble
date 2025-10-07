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
