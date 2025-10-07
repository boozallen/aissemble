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

import static org.technologybrewery.baton.util.FileUtils.replaceInFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;

/**
 * Migration to create the initial helmfile the archetype creates. If projects are upgrading to this version then
 * they will not be running the archetype generation and will not have the default helmfile to add manual actions too
 * . Because projects may choose not to use helmfile, the migration will only run if the helmfile migration enable
 * key is set to true.
 */
public class HelmfileGenerationMigration extends AbstractHelmfileMigration {

    private static final Logger logger = LoggerFactory.getLogger(HelmfileGenerationMigration.class);
    private static final String VERSION_TAG_REGEX = "(.*)(\\$\\{archetypeVersion})";
    private static final String ARTIFACT_ID_TAG_REGEX = "(.*)(\\$\\{artifactId})(.*)";
    public static final List<String> HELMFILE_TEMPLATES = List.of("helmfile.yaml.gotmpl", "helmfile-apps.yaml.gotmpl", "environments.yaml");

    /**
     * Determines whether the migration should execute.
     * Migration will always run if the helmfile migration enable key is set to true.
     *
     * @param file pom file to check
     * @return true if the migration should run
     */
    @Override
    protected boolean shouldExecuteOnFileImpl(File file) {
        return true;
    }

    /**
     * First checks if the helmfiles exist. If not, initialize them.
     *
     * @param file pom file to migrate
     * @return true if migration was successful
     */
    @Override
    protected boolean performMigration(File file) {
        createHelmfileIfNotExist();
        return true;
    }

    private void createHelmfileIfNotExist() throws BatonException {
        for (String filename :HELMFILE_TEMPLATES) {
            String helmfileLocation = getRootProject().getBasedir().getPath() + "/" + filename;
            File file = new File(helmfileLocation);
            if (file.exists()) {
                logger.info(String.format("Helmfile: %s already found. Skipping creation and ignoring migration", filename));
            } else {
                try {
                    InputStream initialHelmfile = getClass().getClassLoader().getResourceAsStream("version_specific/" + filename);
                    if (initialHelmfile == null) {
                        throw new BatonException(String.format("Could not find the helmfile template: %s", filename));
                    }
                    File helmfile = new File(helmfileLocation);
                    FileUtils.copyInputStreamToFile(initialHelmfile, helmfile);
                    if (filename.startsWith("environments")) {
                        updateVersion(helmfile);
                    }

                    MavenProject project = getRootProject();
                    updateParams(helmfile, ARTIFACT_ID_TAG_REGEX, project.getArtifactId());
                } catch (IOException e) {
                    throw new BatonException(String.format("Failed to instantiate the %s", filename), e);
                }
                logger.info(String.format("Initialized root %s", filename));
            }
        }
    }

    private void updateVersion(File file) {
        try (BufferedReader valuesFile = new BufferedReader((new FileReader(file)))) {
            String line;
            Pattern pattern = Pattern.compile(VERSION_TAG_REGEX, Pattern.MULTILINE);

            while ((line = valuesFile.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String substitution = "$1" + getAissembleVersion();
                    replaceInFile(file, VERSION_TAG_REGEX, substitution);
                }
            }
        } catch (IOException e) {
            throw new BatonException("Unable to modify the helmfile's version tag.", e);
        }
    }

    private void updateParams(File file, String regex, String replaceValue) throws IOException {
        String substitution = "$1" + replaceValue + "$3";
        replaceInFile(file, regex, substitution);
    }
}
