package com.boozallen.aissemble.upgrade.migration.version_specific;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */


import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import org.technologybrewery.baton.BatonException;
import org.technologybrewery.baton.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Migration class that updates Poetry package configuration to support Poetry 2.x format for including generated files.
 * Specifically, it modifies the `include` configuration in pyproject.toml files to explicitly specify the package formats
 * (sdist and wheel) for generated source files, since Poetry 2 will only include in sdist by default.
 */
public class Poetry2IncludeMigration extends AbstractAissembleMigration {
    // matches `include = [ "src/____/generated/..." ]`
    private static final String POETRY_INCLUDE_REGEX = "(include\\s*=\\s*\\[\\s*)\"(src/[^/]*/generated[^\"]*)\"(\\s*])";
    private static final String POETRY_INCLUDE_REPLACEMENT = "$1{path = \"$2\", format = [\"sdist\", \"wheel\"]}$3";

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        try {
            return FileUtils.hasRegExMatch(POETRY_INCLUDE_REGEX, file);
        } catch (IOException e) {
            throw new BatonException("Failed to check for include pattern to update for Poetry 2", e);
        }
    }

    @Override
    protected boolean performMigration(File file) {
        try {
            return FileUtils.replaceInFile(file, POETRY_INCLUDE_REGEX, POETRY_INCLUDE_REPLACEMENT);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update pyproject.toml include pattern for Poetry 2", e);
        }
    }
}
