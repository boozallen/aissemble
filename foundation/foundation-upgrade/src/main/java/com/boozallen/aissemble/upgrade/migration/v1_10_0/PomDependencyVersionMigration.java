package com.boozallen.aissemble.upgrade.migration.v1_10_0;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.AbstractPomMigration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;

import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.END;
import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.START;

/**
 * Updates the pom dependencies previously managed by the aiSSEMBLE bom-component to include their necessary versions
 */
public class PomDependencyVersionMigration extends AbstractPomMigration {
    private static final Logger logger = LoggerFactory.getLogger(PomDependencyVersionMigration.class);

    // Uses empty artifactId to indicate any artifact ID. This was determined by searching for the group on Maven
    // Central and ensuring the artifacts were all versioned together.
    private static final List<Dependency> DEPENDENCIES_TO_UPDATE = Arrays.asList(
        dependency("com.amazonaws", "aws-java-sdk-bundle", "${version.aws.sdk.bundle}"),
        dependency("io.delta", "", "${version.delta}"),
        dependency("io.smallrye.config", "smallrye-config", "${version.smallrye.config}"),
        dependency("io.smallrye.reactive", "smallrye-reactive-messaging", "${version.smallrye.reactive.messaging}"),
        dependency("io.vertx", "", "${version.vertx}"),
        dependency("jakarta.ws.rs", "", "${version.jakarta.wr.rs}"),
        dependency("junit", "", "${version.junit}"),
        dependency("org.apache.commons", "commons-math3", null),
        dependency("org.apache.hadoop", "", "${version.hadoop}"),
        dependency("org.apache.spark", "", "${version.spark}"),
        dependency("org.awaitility", "", "${version.awaitility}"),
        dependency("org.codehaus.jackson", "jackson-mapper-asl", "${version.jackson.mapper.asl}"),
        dependency("org.jboss.resteasy", "", "${version.resteasy}"),
        dependency("org.scala-lang", "scala-compiler", "${version.scala}"),
        dependency("org.scala-lang", "scala-dist", "${version.scala}"),
        dependency("org.scala-lang", "scala-library", "${version.scala}"),
        dependency("org.scala-lang", "scala-library-all", "${version.scala}"),
        dependency("org.scala-lang", "scala-reflect", "${version.scala}"),
        dependency("org.scala-lang", "scala-scalap", "${version.scala}"),
        dependency("org.slf4j", "", "${version.slf4j}")
    );

    @Override
    protected boolean shouldExecuteOnFile(File pomFile) {
        Model model = PomHelper.getLocationAnnotatedModel(pomFile);
        return !this.getMatchingDependenciesWithDifferentVersionsForProject(model).isEmpty();
    }

    @Override
    protected boolean performMigration(File pomFile) {
        logger.info("Migrating file to latest aiSSEMBLE dependency versions: {}", pomFile.getAbsolutePath());
        Model model = PomHelper.getLocationAnnotatedModel(pomFile);
        List<Dependency> matchingDependencies = getMatchingDependenciesWithDifferentVersionsForProject(model);
        PomModifications modifications = new PomModifications();

        for (Dependency matchingDependency: matchingDependencies) {
            String updatedVersion = getUpdatedDependencyVersion(matchingDependency);

            if (matchingDependency.getVersion() != null) {
                // Replace the version in place
                if (updatedVersion != null) {
                    modifications.add(replaceInTag(matchingDependency, "version", updatedVersion));
                }
                // Remove the version tag 
                else {
                    modifications.add(deleteTag(matchingDependency, "version"));
                }
            } 
            // Add a new version tag
            else if (updatedVersion != null) {
                int indentSize = matchingDependency.getLocation("artifactId" + START).getColumnNumber() - 1;
                // assumes spaces instead of tabs, but accounting for tabs would be more trouble than it's worth IMO
                String indent = StringUtils.repeat(' ', indentSize);
                modifications.add(new PomModifications.Insertion(matchingDependency.getLocation(END), 0,
                        ignore -> indent + "<version>" + updatedVersion + "</version>\n"));
            }
        }

        if (!modifications.isEmpty()) {
            PomHelper.writeModifications(pomFile, modifications.finalizeMods());
        }
        return true;
    }

    private static Dependency dependency(String groupId, String artifactId, String version) {
        Dependency dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        return dependency;
    }

    /**
     * Returns a list of any dependencies that need to be updated with a new version
     */
    private List<Dependency> getMatchingDependenciesWithDifferentVersionsForProject(Model model) {
        List<Dependency> matchingDependencies = new ArrayList<>();

        // Get all dependencies in the project
        this.getMatchingDependenciesForProject(model, dependency -> true).stream()
            .filter(this::dependencyRequiresUpdate)
            .forEach(matchingDependencies::add);
            
        return matchingDependencies;
    }

    /**
     * Checks if given dependency matches group id and artifact id but not version of the updated dependency
     */
    private boolean dependencyRequiresUpdate(Dependency dependency) {
        return DEPENDENCIES_TO_UPDATE.stream()
            .filter(updatedDependency -> this.isMatchingDependency(dependency, updatedDependency))
            // Return true if the version is not specified (null) when it should be, or the version is specified but different
            .anyMatch(updatedDependency -> 
                (dependency.getVersion() == null && updatedDependency.getVersion() != null ) || 
                (dependency.getVersion() != null && !dependency.getVersion().equals(updatedDependency.getVersion()))
            );
    }

    /**
     * Checks if a dependency matches a template. A dependency matches if it has the same group ID as the template
     * and its artifactId starts with the artifactId of the template dependency
     *
     * @param dependencyToTest
     * @param templateDependency
     * @return true if the `dependencyToTest` has the same group ID as `templateDependency` and the artifactId starts with the artifactId of the template dependency
     */
    private boolean isMatchingDependency(Dependency dependencyToTest, Dependency templateDependency) {
        return dependencyToTest.getGroupId().equals(templateDependency.getGroupId()) &&
               dependencyToTest.getArtifactId().startsWith(templateDependency.getArtifactId());
    }

    /**
     * Returns the updated version for the matching dependency
     */
    private String getUpdatedDependencyVersion(Dependency dependency) {
        return DEPENDENCIES_TO_UPDATE.stream()
            .filter(updatedDependency -> this.isMatchingDependency(dependency, updatedDependency))
            .findFirst()
            .map(Dependency::getVersion)
            .orElse(null);
    }
}