package com.boozallen.aissemble.pipeline.invocation.service;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Pipeline Invocation Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Container class for holding information on each values file for a given spark application
 */
public class SparkApplicationValuesCollection {
    // Key: Classifier.  Value: Path to file
    private Map<String, Path> valuesFiles;

    public SparkApplicationValuesCollection() {
        this.valuesFiles = new HashMap<>();
    }

    /**
     * Adds a values file to this application's registry
     * @param classifier values file classifier, ie dev, ci, base
     * @param file Path of the values file
     */
    public void addValuesFile(String classifier, Path file) {
        this.valuesFiles.put(classifier, file);
    }

    /**
     * @return All detected classifiers for this Spark Application
     */
    public Set<String> getAvailableClassifiers() {
        return this.valuesFiles.keySet();
    }

    /**
     * @param classifier
     * @return Filepath for a given classifier
     */
    public Path getPathForClassifier(String classifier) {
        return this.valuesFiles.get(classifier);
    }
}
