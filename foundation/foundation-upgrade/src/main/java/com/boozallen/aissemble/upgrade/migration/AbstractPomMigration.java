package com.boozallen.aissemble.upgrade.migration;
/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputLocationTracker;
import org.apache.maven.model.Model;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;
import org.technologybrewery.baton.util.pom.PomModifications;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.END;
import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.START;

public abstract class AbstractPomMigration extends AbstractAissembleMigration {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractPomMigration.class);

    public static final String POM = "pom";
    public static final String GUARANTEED_TAG = "<modelVersion>";

    protected String indent;
    
    protected void detectAndSetIndent(File file) {
        try (Stream<String> lines = Files.lines(file.toPath())) {
            indent = lines.filter(line -> line.contains(GUARANTEED_TAG))
                    .findFirst()
                    .map(artifact -> artifact.substring(0, artifact.indexOf(GUARANTEED_TAG)))
                    .orElse(null);
            if (StringUtils.isEmpty(indent)) {
                logger.info("Failed to detect indent for POM. Using default. {}", file);
                indent = "    ";
            }
        } catch (IOException e) {
            throw new BatonException("Failed to get indent from POM:" + file, e);
        }
    }

    /**
     * Checks if a given pom model contains any dependencies with a provided group ID and artifact ID
     */
    protected boolean hasDependency(Model model, String groupId, String artifactId) {
        return model.getDependencies()
             .stream()
             .anyMatch(dependency ->
                 dependency.getGroupId().equals(groupId) &&
                 dependency.getArtifactId().contains(artifactId)
             );
    }

    /**
     * Checks the dependency and dependency management section of a given pom model and all profiles for any matches
     */
    protected List<Dependency> getMatchingDependenciesForProject(Model model, Predicate<? super Dependency> filter) {
        List<Dependency> matchingDependencies = this.getMatchingDependencies(model, filter);
        for (Profile profile : model.getProfiles()) {
            matchingDependencies.addAll(this.getMatchingDependencies(profile, filter));
        }
        return matchingDependencies;
    }

    /**
     * Checks the dependency and dependency management section of a given pom model for any matches
     */
    private List<Dependency> getMatchingDependencies(ModelBase model, Predicate<? super Dependency> filter) {
        List<Dependency> matchingDependencies = new ArrayList<>();

        Stream.concat(
            model.getDependencies()
                .stream()
                .filter(filter),
            Optional.ofNullable(model.getDependencyManagement()).map(dependencyManagement -> dependencyManagement.getDependencies()
                .stream()
                .filter(filter))
                .orElse(Stream.empty())
        )
        .forEach(matchingDependencies::add);
    
        return matchingDependencies;
    }

    protected static PomModifications.Replacement replaceInTag(InputLocationTracker container, String tag, String contents) {
        InputLocation start = container.getLocation(tag);
        InputLocation end = container.getLocation(tag + END);
        end = new InputLocation(end.getLineNumber(), end.getColumnNumber() - tag.length() - "</>".length(), end.getSource());
        return new PomModifications.Replacement(start, end, contents);
    }

    protected static PomModifications.Deletion deleteTag(InputLocationTracker container, String tag) {
        InputLocation start = container.getLocation(tag + START);
        InputLocation end = container.getLocation(tag + END);
        return new PomModifications.Deletion(start, end);
    }
}