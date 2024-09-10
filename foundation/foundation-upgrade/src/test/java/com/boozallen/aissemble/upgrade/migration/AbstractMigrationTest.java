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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class AbstractMigrationTest {
    protected File testFile;
    protected boolean shouldExecute;
    protected boolean successful;

    private static final Path TEST_FILES_FOLDER = Paths.get("target", "test-classes", "test-files");
    private static final String MIGRATION_DIR = File.separator + "migration" + File.separator;
    private static final String VALIDATION_DIR = File.separator + "validation" + File.separator;

    protected static void assertLinesMatch(String message, File expectedFile, File actualFile) {
        try {
            List<String> actualLines = Files.readAllLines(actualFile.toPath());
            List<String> expectedLines = Files.readAllLines(expectedFile.toPath());
            for (int i = 0; i < actualLines.size() || i < expectedLines.size(); i++) {
                String actualLine = null;
                String expectedLine = null;
                if (i < actualLines.size()) {
                    actualLine = actualLines.get(i);
                }
                if (i < expectedLines.size()) {
                    expectedLine = expectedLines.get(i);
                }
                assertEquals(message + ": Ln " + (i+1), expectedLine, actualLine);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to compare test and validation file contents", e);
        }
    }

    protected static void addTestFile(String subPath) throws IOException {
        File testFile = TEST_FILES_FOLDER.resolve(subPath).toFile();
        if(testFile.exists()) {
            throw new RuntimeException(String.format("Test file at %s already exists", subPath));
        }
        testFile.getParentFile().mkdirs();
        testFile.createNewFile();
    }

    protected static File getTestFile(String subPath) {
        if (subPath.startsWith(File.separator)) {
            subPath = subPath.substring(1);
        }
        File testFile = TEST_FILES_FOLDER.resolve(subPath).toFile();
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

    protected void assertMigrationSuccess() {
        assertTrue("Migration was incorrectly skipped", shouldExecute);
        assertTrue("Migration did not execute successfully", successful);
    }

    protected void assertMigrationSkipped() {
        assertFalse("Migration was not skipped as expected", shouldExecute);
    }

    /**
     * Asserts that the test file matches the expected validation file. Calculates the validation file path based on the
     * current test file path by replacing the migration directory with the validation directory. Throws an exception if
     * this calculation fails or if the validation file does not exist.
     *
     * @param message the message to display if the assertion fails
     */
    protected void assertTestFileMatchesExpectedFile(String message) {
        String validationFilePath = testFile.getPath().replace(MIGRATION_DIR, VALIDATION_DIR);
        File validationFile = new File(validationFilePath);
        if( testFile.equals(validationFile)) {
            throw new RuntimeException(String.format("Test/validation files not using expected structure: %s", testFile));
        }
        if(!validationFile.exists()) {
            throw new RuntimeException(String.format("Validation file does not exist: %s", validationFilePath));
        }
        assertLinesMatch(message, validationFile, testFile);
    }
}
