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
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;

public class InferenceDockerPomMigration extends AbstractContainerizeMigration {

    public static final String AISSEMBLE_INFERENCE_DOCKER = "aissemble-inference-docker";
    // Setting DOCKER_USER to null equates to using the default value
    protected static final String DOCKER_USER = null;

    /**
     * Determines if the migration should be executed. Will return true if the inference docker pom does not use the
     * Habushu containerization goal
     *
     * @param file file to check
     * @return true if the migration should run
     */
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        MavenProject mavenProject = getMavenProject();
        return isDockerBuildPackage(mavenProject) && containsFermenterProfile(mavenProject, AISSEMBLE_INFERENCE_DOCKER)
                && !containsHabushuContainerizeGoal(mavenProject);
    }

    /**
     * Performs the migration. Inserts the habushu-maven-plugin into the projects plugin list
     *
     * @param file POM file to insert the plugin into
     * @return true if the migration was successful
     */
    @Override
    protected boolean performMigration(File file) {
        detectAndSetIndent(file);
        Model model = PomHelper.getLocationAnnotatedModel(file);
        PomModifications pomModifications = new PomModifications();

        // Safe to assume the pom has build and plugins because "shouldExecuteOnFile" checks the fermenter plugin
        String dockerBase = "${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-nvidia:${VERSION_AISSEMBLE}";
        final String insertContent = habushuPluginWithContainerizationGoal(dockerBase, dockerBase, DOCKER_USER, 3);
        pomModifications.add(new PomModifications.Insertion(model.getBuild().getLocation("plugins" + END),
                3, content -> insertContent));
        return PomHelper.writeModifications(file, pomModifications.finalizeMods());
    }
}
