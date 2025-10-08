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

import io.jsonwebtoken.lang.Strings;
import io.quarkus.runtime.Startup;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Helper bean for detecting and managing available SparkApplication values files on the service's filesystem
 */
@Startup
@ApplicationScoped
public class ValuesFileRegistry {
    public static final String DEFAULT_WORKING_DIR = "/deployments/sparkApplicationValues";
    public static final String WORKING_DIR_CONFIG_STRING = "service.pipelineInvocation.values.dir";

    private static final Logger logger = Logger.getLogger(ValuesFileRegistry.class);

    // Key: SparkApplication name (kebab-case).  Value: Collection of all values files mapped to that name.
    private Map<String, SparkApplicationValuesCollection> index;

    /**
     * Private constructor to ensure singleton status
     * @param workingDir Not currently intended for human modification.  Present for future-proofing.
     * @throws IOException
     */
    private ValuesFileRegistry(
            @ConfigProperty(name = WORKING_DIR_CONFIG_STRING, defaultValue = DEFAULT_WORKING_DIR)
            String workingDir
            ) throws IOException {
        this.index = new HashMap<>();
        this.discoverValuesFiles(Path.of(workingDir));
        this.logDiscoveredValuesFiles();
    }

    /**
     * Logs information about the discovered applications and respective classifiers.
     */
    private void logDiscoveredValuesFiles() {
        for(String app : this.index.keySet()) {
            String classifiers = Strings.collectionToCommaDelimitedString(index.get(app).getAvailableClassifiers());
            logger.info("Discovered application " + app + " with classifiers [" + classifiers + "]");
        }
    }

    /**
     * Searches the specified filesystem to build an index of available sparkapplications and their associated values
     * files.
     * @param workingDir Directory in which to search for values files
     * @throws IOException
     */
    private void discoverValuesFiles(Path workingDir) throws IOException {
        Files.walk(workingDir, 1)
                .filter((file -> file.getFileName().toString().endsWith(".yaml")))
                .forEach((detectedFile -> {
                    String fileNameNoExt = detectedFile.getFileName().toString().replace(".yaml", "");
                    String[] components = fileNameNoExt.split("-");
                    // Expected filename format:
                    // [pipeline-name-hyphenated]-[classifier]-values.yaml
                    int classifierIndex = components.length - 2;
                    String classifier = components[classifierIndex];
                    String baseNameHyphenated =
                            String.join("-", Arrays.copyOfRange(components, 0, classifierIndex)).toLowerCase();
                    if(!index.containsKey(baseNameHyphenated)) {
                        index.put(baseNameHyphenated, new SparkApplicationValuesCollection());
                    }
                    index.get(baseNameHyphenated).addValuesFile(classifier, detectedFile);
        }));
    }

    /**
     * @param applicationName Target application name
     * @return Containment structure with associated values file metadata
     */
    public SparkApplicationValuesCollection getValuesCollection(String applicationName) {
        return this.index.get(applicationName);
    }

    /**
     * @return Collection of available spark application names
     */
    public Collection<String> getAvailableSparkApplications() {
        return this.index.keySet();
    }
}
