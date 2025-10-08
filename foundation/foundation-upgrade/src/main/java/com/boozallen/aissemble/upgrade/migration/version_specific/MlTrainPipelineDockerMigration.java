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

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.END;

import java.io.File;

import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;


/**
 * Migration that updates the POM files for machine learning pipelines with a train step to use Habushu's containerize-dependencies goal.
 */
public class MlTrainPipelineDockerMigration extends AbstractContainerizeMigration {
    protected static final Logger logger = LoggerFactory.getLogger(MlTrainPipelineDockerMigration.class);
    private static final String AISSEMBLE_TRAINING_DOCKER = "aissemble-training-docker";
    // Setting DOCKER_USER to null equates to using the default value
    protected static final String DOCKER_USER = null;

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        // Should migrate if the pom package is docker-build, the fermenter plugin is for aissemble training, and it
        // does not already have the containerize plugin
        MavenProject mavenProject = getMavenProject();
        return isDockerBuildPackage(mavenProject) && containsFermenterProfile(mavenProject, AISSEMBLE_TRAINING_DOCKER) && !containsHabushuContainerizeGoal(mavenProject);
    }

    @Override
    protected boolean performMigration(File file) {
        Model model = PomHelper.getLocationAnnotatedModel(file);
        detectAndSetIndent(file);
        PomModifications modifications = new PomModifications();

        String dockerBase = "${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-nvidia:${VERSION_AISSEMBLE}";
        String content = habushuPluginWithContainerizationGoal(dockerBase, dockerBase, DOCKER_USER, 3);
        modifications.add(new PomModifications.Insertion(model.getBuild().getLocation("plugins" + END), 3, ignore -> content));

        return PomHelper.writeModifications(file, modifications.finalizeMods());
    }
}
