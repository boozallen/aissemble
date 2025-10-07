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

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import com.boozallen.aissemble.upgrade.migration.AbstractPomMigration;

public abstract class AbstractContainerizeMigration extends AbstractPomMigration {

    public static final String HABUSHU_MAVEN_PLUGIN_ID = "org.technologybrewery.habushu:habushu-maven-plugin";
    private static final String FERMENTER_MDA_PLUGIN_ID = "org.technologybrewery.fermenter:fermenter-mda";
    private static final String DOCKER_BUILD_TYPE = "docker-build";
    private static final String PROFILE = "profile";
    private static final String HABUSHU = "habushu";

    protected boolean containsHabushuContainerizeGoal(MavenProject mavenProject) {
        Plugin habushuPlugin = mavenProject.getPlugin(HABUSHU_MAVEN_PLUGIN_ID);
        if (habushuPlugin != null) {
            for (PluginExecution execution : habushuPlugin.getExecutions()) {
                if (execution.getGoals().contains("containerize-dependencies")) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isDockerBuildPackage(MavenProject mavenProject) {
        return StringUtils.equals(DOCKER_BUILD_TYPE, mavenProject.getModel().getPackaging());
    }

    protected boolean containsFermenterProfile(MavenProject mavenProject, String profileId) {
        Plugin fermenterMdaPlugin = mavenProject.getPlugin(FERMENTER_MDA_PLUGIN_ID);
        if (fermenterMdaPlugin != null) {
            Xpp3Dom configuration = (Xpp3Dom) fermenterMdaPlugin.getConfiguration();

            for (Xpp3Dom child: configuration.getChildren()) {
                if(PROFILE.equals(child.getName()) && profileId.equals(child.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected String habushuPluginWithContainerizationGoal(String dockerBuilderBase, String dockerFinalBase, String dockerUser, int indentCount){
        StringBuilder builder = new StringBuilder();
        builder.append(repeat(indent, indentCount)).append("<plugin>\n")
                .append(repeat(indent, indentCount+1)).append("<groupId>org.technologybrewery.habushu</groupId>\n")
                .append(repeat(indent, indentCount+1)).append("<artifactId>habushu-maven-plugin</artifactId>\n")
                .append(repeat(indent, indentCount+1)).append("<executions>\n")
                .append(repeat(indent, indentCount+2)).append("<execution>\n")
                .append(repeat(indent, indentCount+3)).append("<id>dockerize</id>\n")
                .append(repeat(indent, indentCount+3)).append("<goals>\n")
                .append(repeat(indent, indentCount+4)).append("<goal>containerize-dependencies</goal>\n")
                .append(repeat(indent, indentCount+3)).append("</goals>\n")
                .append(repeat(indent, indentCount+3)).append("<phase>prepare-package</phase>\n")
                .append(repeat(indent, indentCount+3)).append("<configuration>\n")
                .append(repeat(indent, indentCount+4)).append("<dockerfile>src/main/resources/docker/Dockerfile</dockerfile>\n")
                .append(repeat(indent, indentCount+4)).append("<dockerBuilderBase>" + dockerBuilderBase + "</dockerBuilderBase>\n")
                .append(repeat(indent, indentCount+4)).append("<dockerFinalBase>" + dockerFinalBase + "</dockerFinalBase>\n");

        if(dockerUser != null){
            builder.append(repeat(indent, indentCount+4)).append("<dockerUser>" + dockerUser + "</dockerUser>\n");
        }

        builder.append(repeat(indent, indentCount+3)).append("</configuration>\n")
                .append(repeat(indent, indentCount+2)).append("</execution>\n")
                .append(repeat(indent, indentCount+1)).append("</executions>\n")
                .append(repeat(indent, indentCount)).append("</plugin>\n");
        return builder.toString();
    }

    /**
     * Checks for and `habushu` packaged dependencies exists.
     * @param mavenProject the maven project to check against
     * @return true if any `habushu` packaged dependencies exist
     */
    protected boolean containerHabushuPackagedDependency(MavenProject mavenProject) {
        for (Dependency dependency : mavenProject.getDependencies()) {
            if(HABUSHU.equals(dependency.getType())) {
                return true;
            }
        }
        return false;
    }
}
