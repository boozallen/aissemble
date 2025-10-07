package com.boozallen.aissemble.pipeline.invocation.service;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Pipeline Invocation Service
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
