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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;

import com.boozallen.aissemble.upgrade.migration.AbstractPomMigration;

/**
 * For all Habushu modules, ensures that dependencies on other Habushu modules in the same repository are of type `habushu` instead of type `pom`.
 */
public class HabushuMonorepoDependencyMigration extends AbstractPomMigration {

    private static final String POM = "pom";
    private static final String HABUSHU = "habushu";

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        if (isHabushuProject(getMavenProject())) {
            Map<String, MavenProject> gavMap = getGavToProjectMap();
            for (Dependency dependency : getMavenProject().getDependencies()) {
                if (POM.equals(dependency.getType())) {
                    MavenProject project = gavMap.get(toGav(dependency));
                    if (project == null) {
                        logger.warn("Could not find local project for dependency: " + toGav(dependency));
                    } else if(isHabushuProject(project)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected boolean performMigration(File file) {
        Map<String, MavenProject> gavMap = getGavToProjectMap();
        Model model = PomHelper.getLocationAnnotatedModel(file);
        PomModifications modifications = new PomModifications();
        for (Dependency dependency : model.getDependencies()) {
            String gav = toGav(dependency);
            MavenProject project = gavMap.get(gav);
            if (POM.equals(dependency.getType()) && isHabushuProject(project)) {
                modifications.add(replaceInTag(dependency, "type", "habushu"));
            }
        }
        return PomHelper.writeModifications(file, modifications.finalizeMods());
    }

    private Map<String, MavenProject> getGavToProjectMap() {
        Map<String, MavenProject> gavMap = new HashMap<>();
        for (MavenProject project : getRootProject().getCollectedProjects()) {
            gavMap.put(toGav(project), project);
        }
        return gavMap;
    }

    private static boolean isHabushuProject(MavenProject project) {
        return project != null && HABUSHU.equals(project.getPackaging());
    }
}
