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
import org.technologybrewery.baton.BatonException;
import org.technologybrewery.baton.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * This migration updates the pyproject.toml file to remove the data encryption dependencies that are no longer supported.
 */
public class DataEncryptionRemovalPyprojectMigration extends AbstractPomMigration {

    public static final String DATA_ENCRYPTION_DEPENDENCIES = "^aissemble-(foundation-encryption-policy|extensions-encryption-vault)-python\\s*=.*$";

    @Override
    protected boolean shouldExecuteOnFile(File pyproject) {
        try {
            return FileUtils.hasRegExMatch(DATA_ENCRYPTION_DEPENDENCIES, pyproject);
        } catch (IOException e) {
            throw new BatonException("Could not check pyproject.toml for data encryption dependencies: " + pyproject.getPath(), e);
        }
    }

    @Override
    protected boolean performMigration(File pyproject) {
        try {
            List<String> lines = Files.readAllLines(pyproject.toPath());
            List<String> updateLines = new ArrayList<>();
            boolean update = false;
            for (String line : lines) {
                if (line.matches(DATA_ENCRYPTION_DEPENDENCIES)) {
                    update = true;
                } else {
                    updateLines.add(line);
                }
            }
            if (update) {
                Files.write(pyproject.toPath(), updateLines);
                return true;
            }
        } catch (IOException e) {
            throw new BatonException("Failed to update data encryption dependencies in: " + pyproject.getPath(), e);
        }
        return false;
    }
}
