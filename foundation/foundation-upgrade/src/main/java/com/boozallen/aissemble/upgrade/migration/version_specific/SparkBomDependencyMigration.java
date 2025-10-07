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
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;

import java.io.File;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.END;

public class SparkBomDependencyMigration extends AbstractPomMigration {

    @Override
    protected boolean shouldExecuteOnFile(File pomFile) {
        Model model = PomHelper.getLocationAnnotatedModel(pomFile);
        return hasSparkDependency(model) && hasAissembleBuildParent(getRootProject()) && !hasSparkBomDependencyManagement(model);
    }

    @Override
    protected boolean performMigration(File pomFile) {
        Model model = PomHelper.getLocationAnnotatedModel(pomFile);
        detectAndSetIndent(pomFile);
        PomModifications modifications = new PomModifications();
        String sparkBomDependency = buildSparkBomDependency();
        // If <dependencyManagement> exists, insert into <dependencies>
        if (model.getDependencyManagement() != null) {
            modifications.add(new PomModifications.Insertion(model.getDependencyManagement().getLocation("dependencies" + END), 1, ignore -> sparkBomDependency));
        } else {
            // If <dependencyManagement> doesn't exist, create it here add it before <dependencies>
            String depsManagementBlock =
                    repeat(indent, 1) + "<dependencyManagement>\n" +
                    repeat(indent, 2) + "<dependencies>\n" +
                    sparkBomDependency +
                    repeat(indent, 2) + "</dependencies>\n" +
                    repeat(indent, 1) + "</dependencyManagement>\n\n";
            modifications.add(new PomModifications.Insertion(model.getLocation("dependencies"), 1, ingnore -> depsManagementBlock));
        }
        return PomHelper.writeModifications(pomFile, modifications.finalizeMods());
    }

    private boolean hasSparkDependency(Model model) {
        return model.getDependencies().stream()
                .anyMatch(dep -> dep.getGroupId().equals("org.apache.spark"));
    }

    private boolean hasAissembleBuildParent(MavenProject rootProject) {
        return rootProject.getParent().getArtifactId().equals(AISSEMBLE_PARENT);
    }

    private boolean hasSparkBomDependencyManagement(Model model) {
        if (model.getDependencyManagement() == null || model.getDependencyManagement().getDependencies() == null) {
            return false;
        }
        return model.getDependencyManagement().getDependencies().stream()
                .anyMatch(dep -> dep.getGroupId().equals("com.boozallen.aissemble")
                        && dep.getArtifactId().equals("aissemble-spark-bom"));
    }

    private String buildSparkBomDependency(){
        StringBuilder builder = new StringBuilder();
        builder.append(repeat(indent, 3)).append("<dependency>\n")
               .append(repeat(indent, 4)).append("<groupId>com.boozallen.aissemble</groupId>\n")
               .append(repeat(indent, 4)).append("<artifactId>aissemble-spark-bom</artifactId>\n")
               .append(repeat(indent, 4)).append("<version>${version.aissemble}</version>\n")
               .append(repeat(indent, 4)).append("<type>pom</type>\n")
               .append(repeat(indent, 4)).append("<scope>import</scope>\n")
               .append(repeat(indent, 3)).append("</dependency>\n");
        return builder.toString();
    }
}
