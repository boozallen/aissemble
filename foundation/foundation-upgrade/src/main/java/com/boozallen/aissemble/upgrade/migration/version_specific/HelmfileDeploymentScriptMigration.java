package com.boozallen.aissemble.upgrade.migration.version_specific;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
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

import static org.technologybrewery.baton.util.FileUtils.replaceInFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.apache.maven.project.MavenProject;
import org.technologybrewery.baton.BatonException;
import org.technologybrewery.baton.util.FileUtils;

public class HelmfileDeploymentScriptMigration extends AbstractHelmfileMigration {

    private static final String JENKINS_PIPELINE_STEPS = "jenkinsPipelineSteps.groovy";
    private static final String DEF_JENKINS_STEPS = "def jenkinsSteps";
    private static final String HELMFILE_DEPLOY_SCRIPT = "version_specific/JenkinsfileDeploy.groovy";
    private static final String PROJECT_NAME_TAG_REGEX = "(.*)(\\$\\{projectName})(.*)";
    private static final String PROJECT_SCM_TAG_REGEX = "(.*)(\\$\\{projectGitUrl})(.*)";
    private static final String ARTIFACT_TAG_REGEX = "(.*)(\\$\\{rootArtifactId})(.*)";

    /**
     * Determines if the migration should run. Will always execute to delete the deprecated jenkins steps file. Will
     * assume if the deployment is using the Jenkins steps then it also needs to be updated.
     * @param file file to check
     * @return true if migration should execute
     */
    @Override
    protected boolean shouldExecuteOnFileImpl(File file) {
        if (file.getName().equalsIgnoreCase(JENKINS_PIPELINE_STEPS)) {
            return true;
        }
        try {
            return FileUtils.hasRegExMatch(DEF_JENKINS_STEPS, file);
        } catch (IOException e) {
            throw new BatonException("Failed to parse Jenkins deploy script", e);
        }
    }

    /**
     * Perform the migration. It will delete the jenkinsPipelineSteps file and replace the JenkinsfileDeploy content with
     * the new helmfile implementation
     * @param file file to migrate
     * @return true if successfully migrated
     */
    @Override
    protected boolean performMigration(File file) {
        // If the file is jenkinsPipelineSteps.groovy then delete it
        if (file.getName().equalsIgnoreCase(JENKINS_PIPELINE_STEPS)){
            try {
                return Files.deleteIfExists(file.toPath());
            } catch (IOException e) {
                throw new BatonException("Failed to delete the Jenkins pipeline steps file", e);
            }
        }

        // Otherwise the file is JenkinsfileDeploy.groovy
        // Delete the existing jenkins deploy script
        file.delete();

        // Then recreate it with the helmfile deployment
        InputStream initialHelmfile = getClass().getClassLoader().getResourceAsStream(HELMFILE_DEPLOY_SCRIPT);
        if (initialHelmfile == null) {
            throw new BatonException("Could not find the helmfile deploy script");
        }
        try {
            org.apache.commons.io.FileUtils.copyInputStreamToFile(initialHelmfile, file);

            MavenProject project = getRootProject();
            String projectName = project.getName() != null ? project.getName() : project.getArtifactId();
            updateParams(file, PROJECT_NAME_TAG_REGEX, projectName);
            updateParams(file, PROJECT_SCM_TAG_REGEX, project.getScm().getUrl());
            updateParams(file, ARTIFACT_TAG_REGEX, project.getArtifactId());
        } catch (IOException e) {
            throw new BatonException("Failed to update JenkinsfileDeploy.groovy", e);
        }
        return true;
    }

    private void updateParams(File file, String regex, String replaceValue) throws IOException {
        String substitution = "$1" + replaceValue + "$3";
        replaceInFile(file, regex, substitution);
    }
}
