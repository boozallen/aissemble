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

import java.io.File;
import java.nio.file.Paths;

public class AbstractMigrationTest {
    protected File testFile;
    protected boolean shouldExecute;
    protected boolean successful;

    protected static File getTestFile(String subPath) {
        File testFile = Paths.get("target", "test-classes", "test-files", subPath).toFile();
        File dir = testFile.getParentFile();
        if (!dir.mkdirs() && !dir.isDirectory()) {
            throw new RuntimeException("Parent directory of test file is already a regular file: " + dir);
        }
        return testFile;
    }

    protected void performMigration(AbstractAissembleMigration migration) {
        shouldExecute = migration.shouldExecuteOnFile(testFile);
        successful = shouldExecute && migration.performMigration(testFile);
    }
}
