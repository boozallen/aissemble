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

import org.technologybrewery.baton.BatonException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * This migration unwraps the aissemble-fastapi-chart configurations from the values files.
 */
public class FastapiDependencyRemovalValuesMigration extends AbstractAissembleMigration {
    private static final Pattern AISSEMBLE_FASTAPI_CHART = Pattern.compile("^\\s*aissemble-fastapi-chart:\\s*$");
    private static final Pattern AISSEMBLE_VERSIONING_CHART = Pattern.compile("^\\s*aissemble-versioning-chart:\\s*$");

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        try (Stream<String> lines = Files.lines(file.toPath())) {
            return lines.anyMatch(line -> AISSEMBLE_FASTAPI_CHART.matcher(line).matches());
        }
        catch (IOException e) {
            throw new BatonException("Failed to read YAML file: " + file.getPath(), e);
        }
    }

    /**
     * Executes the migration on a given YAML file by removing the
     * <code>aissemble‑fastapi‑chart:</code> block header and unindenting the lines under it.
     *
     * @param file YAML file to migrate
     * @return true if migration was successful
     * @throws org.technologybrewery.baton.BatonException if an error occurs during file read or write
     */
    @Override
    protected boolean performMigration(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            List<String> out = new ArrayList<>();
            boolean inVersioningSection = false;
            boolean inFastApiSection = false;
            int baseIndent = -1;
            final int UNINDENT_AMOUNT = 2;

            for (String line : lines) {
                int indent = line.length() - line.stripLeading().length();
                //Make sure we encounter the aissemble-versioning-chart section first
                if (AISSEMBLE_VERSIONING_CHART.matcher(line).matches()) {
                    inVersioningSection = true;
                }
                // If we are in the aissemble-versioning-chart section, we want to skip the aissemble-fastapi-chart line
                if (AISSEMBLE_FASTAPI_CHART.matcher(line).matches() && inVersioningSection) {
                    inFastApiSection = true;
                    baseIndent = line.length() - line.stripLeading().length();
                    continue;
                }
                //Check if we left the aissemble-fastapi-chart section
                if (inFastApiSection && (indent <= baseIndent && !line.trim().isEmpty())) {
                    inFastApiSection = false;
                }
                //Unindent the lines in the aissemble-fastapi-chart section
                if (inFastApiSection && (indent > baseIndent)) {
                        int newIndent = Math.max(0, indent - UNINDENT_AMOUNT);
                        line = " ".repeat(newIndent) + line.stripLeading();
                }
                out.add(line);
            }
            Files.write(file.toPath(), out, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException e) {
            throw new BatonException("Error removing aissemble-fastapi-chart dependency in values.yaml: " + file.getPath(), e);
        }
    }
}
