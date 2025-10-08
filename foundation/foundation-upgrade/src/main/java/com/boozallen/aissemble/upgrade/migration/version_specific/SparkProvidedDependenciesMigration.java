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

import com.boozallen.aissemble.upgrade.migration.AbstractPomMigration;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.END;

/**
 * This migration accounts for the update to extensions-data-delivery-spark to set all dependencies provided by Spark to
 * `<scope>provided</scope>`.  Because of this change, any project that was using those dependencies transitively must
 * now explicitly declare them.  This migration assumes that any project with a dependency on `extensions-data-delivery`
 * or any other baseline module that depended on `extensions-data-delivery` was using all the updated dependencies, as
 * trying to be more accurate would be very complex with little benefit.
 */
public class SparkProvidedDependenciesMigration extends AbstractPomMigration {
    private static final List<String> UPDATED_MODULES = List.of(
            "extensions-data-delivery-spark",
            "extensions-data-delivery-spark-neo4j",
            "extensions-data-delivery-spark-postgres",
            "extensions-transform-spark-java");

    @Override
    protected boolean shouldExecuteOnFile(File pomFile) {
        Model model = PomHelper.getLocationAnnotatedModel(pomFile);
        if (model.getPackaging().equals("jar")) {
            return dependsOnUpdatedProject(model) && !getDependenciesToBeAdded(model).isEmpty();
        }
        return false;
    }

    @Override
    protected boolean performMigration(File pomFile) {
        Model model = PomHelper.getLocationAnnotatedModel(pomFile);
        detectAndSetIndent(pomFile);
        List<String> toAdd = getDependenciesToBeAdded(model);
        PomModifications modifications = new PomModifications();
        String content = getDependencies(toAdd);
        modifications.add(new PomModifications.Insertion(model.getLocation("dependencies" + END), 2, ignore -> content));

        return PomHelper.writeModifications(pomFile, modifications.finalizeMods());
    }

    private List<String> getDependenciesToBeAdded(Model model) {
        List<String> depsToAdd = getAllNewDependencies();
        for (Dependency dependency : model.getDependencies()) {
            if ("org.apache.spark".equals(dependency.getGroupId())) {
                depsToAdd.remove(dependency.getArtifactId());
            }
        }
        return depsToAdd;
    }

    protected boolean dependsOnUpdatedProject(Model model) {
        List<Dependency> dependencies = model.getDependencies();
        for (Dependency dependency : dependencies) {
            if ("com.boozallen.aissemble".equals(dependency.getGroupId()) &&
                    UPDATED_MODULES.contains(dependency.getArtifactId())) {
                return true;
            }
        }
        return false;
    }

    private String getDependencies(List<String> dependencies) {
        StringBuilder builder = new StringBuilder();
        for (String depArtifactId : dependencies) {
            builder.append(repeat(indent, 2)).append("<dependency>\n")
                    .append(repeat(indent, 3)).append("<groupId>org.apache.spark</groupId>\n")
                    .append(repeat(indent, 3)).append("<artifactId>").append(depArtifactId).append("</artifactId>\n")
                    .append(repeat(indent, 2)).append("</dependency>\n");
        }
        return builder.toString();
    }

    private List<String> getAllNewDependencies() {
        List<String> dependenciesList = new ArrayList<>();
        dependenciesList.add("spark-core_2.12");
        dependenciesList.add("spark-hive_2.12");
        dependenciesList.add("spark-streaming_2.12");
        dependenciesList.add("spark-sql_2.12");
        dependenciesList.add("spark-catalyst_2.12");
        return dependenciesList;
    }
}
