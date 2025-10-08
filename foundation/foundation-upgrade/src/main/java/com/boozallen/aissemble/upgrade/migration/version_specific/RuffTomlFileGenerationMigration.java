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

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Migration to create the initial ruff.toml file the archetype creates. If projects are upgrading to this version then
 * they will not be running the archetype generation and will not have the default  ruff.toml file to add manual actions too
 * . Because projects may choose not to use  ruff.toml file, the migration will only run if the  ruff.toml file migration enable
 * key is set to true.
 */
public class RuffTomlFileGenerationMigration extends AbstractAissembleMigration {
    private static final Logger logger = LoggerFactory.getLogger(RuffTomlFileGenerationMigration.class);
    public static final String RUFF_TOML_FILE = "ruff.toml";
    private String ruffFileLocation;

    /**
     * Determines whether the migration should execute.
     * Migration will run if the there is a hubushu module in the project.
     *
     * @param file pom file to check
     * @return true if the migration should run
     */
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        return hasHabushuModuleAndRuffTolmFileDoesNotAlreadyExist(getRootProject());
    }

    /**
     * First checks if the  ruff.toml files exist. If not, initialize it.
     *
     * @param file pom file to migrate
     * @return true if migration was successful
     */
    @Override
    protected boolean performMigration(File file) {
        createRuffTomlFileIfNotExist();
        return true;
    }

    private void createRuffTomlFileIfNotExist() throws BatonException {
        try {
            InputStream initialRuffTomlFile = getClass().getClassLoader().getResourceAsStream("version_specific/" + RUFF_TOML_FILE);

            if (initialRuffTomlFile == null) {
                throw new BatonException("Could not find the ruff.toml template");
            }
            File ruffTomlFile = new File(getRuffTomlFileLocation());
            FileUtils.copyInputStreamToFile(initialRuffTomlFile, ruffTomlFile);
        } catch (IOException e) {
            throw new BatonException("Failed to instantiate the riff.toml file", e);
        }
        logger.info("Initialized root ruff.toml");
    }

    /**
     * Checks if the ruff.toml file already exists and if the project has a habushu sub-module
     * @param mavenProject The maven project object
     * @return if there is no ruff.toml file and the project has a habushu sub-module
     */
    protected boolean hasHabushuModuleAndRuffTolmFileDoesNotAlreadyExist(MavenProject mavenProject) {
        boolean hasHabushuModule = false;

        try {
            hasHabushuModule = mavenProject.getCollectedProjects().stream().anyMatch(proj -> proj.getPackaging().equals("habushu"));
        } catch (Exception e) {
            logger.warn("Unable to check for habushu modules");
        }

        return hasHabushuModule && !ruffTomlFileExists();
    }

    private boolean ruffTomlFileExists() {
        File ruffTolmFile = new File(getRuffTomlFileLocation());

        return ruffTolmFile.exists();
    }

    private String getRuffTomlFileLocation() {
        if(ruffFileLocation == null){
            ruffFileLocation = getRootProject().getBasedir().getPath() + "/" + RUFF_TOML_FILE;
        }
        
        return ruffFileLocation;
    }
}
