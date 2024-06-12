package com.boozallen.aissemble.upgrade.migration.v1_7_0;

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
import com.boozallen.aissemble.upgrade.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DockerPipInstallMigration extends AbstractAissembleMigration {

    private static final Logger logger = LoggerFactory.getLogger(DockerPipInstallMigration.class);
    // starts with a negative look-ahead to ensure we are not matching lines that already contain
    // "RUN set -e &&"
    private static final String forDoPattern = "^(?!.*RUN set -e &&)RUN .*for .+; do .+; done";
    // () denote various capture groups to separate relevant portions of the regex match
    // (?:) enables the enclosed portion of the regex pattern to not be evaluated as a capture group
    private static final String findExecPattern =
            "(^RUN )(find )(.*)( -exec)(.*python.* install (?:-\\S*\\s)*)(.+)";
    private static final String RUN = "RUN ";
    private static final String RUN_WITH_SET_E = "RUN set -e && ";
    private static final String FILES_FIND = "files=$(find ";
    private static final String FOR_FILE_IN_FILES_DO = "); for file in $files; do";
    private static final String FILE_OR_EXIT_1_DONE = "$file || exit 1; done;";
    private static final String upgradableInstructionsPattern =
            String.join("|", findExecPattern, forDoPattern);

    /**
     * @param dockerFile the file to check
     * @return boolean denoting if the dockerfile has at least one instruction to be migrated
     */
    @Override
    protected boolean shouldExecuteOnFile(File dockerFile) {
        boolean shouldExecute = false;

        if (dockerFile != null && dockerFile.exists()) {
            try {
                shouldExecute = FileUtils.hasRegExMatch(upgradableInstructionsPattern, dockerFile);
            } catch (IOException e) {
                logger.error(String.format("Unable to load '%s' due to exception:", dockerFile.getAbsolutePath()), e);
            }
        }

        return shouldExecute;
    }

    /**
     * @param dockerFile the file to update instruction(s)
     * @return boolean denoting if the dockerfile has had at least one instruction migrated
     */
    @Override
    protected boolean performMigration(File dockerFile) {
        logger.info("Migrating file: {}", dockerFile.getAbsolutePath());
        boolean migratedSuccessfully = false;

        try {
            // first, migrate for-do pattern instruction(s)
            boolean forDoMigratedSuccessfully = FileUtils.modifyRegexMatchInFile(
                    dockerFile,
                    forDoPattern,
                    RUN,
                    RUN_WITH_SET_E
            );

            // second, migrate find-exec pattern instruction(s)
            boolean findExecMigratedSuccessfully =
                    migrateFindExecInstructions(dockerFile);
            migratedSuccessfully = forDoMigratedSuccessfully || findExecMigratedSuccessfully;
        } catch (Exception e) {
            logger.error(String.format("Unable to migrate '%s' due to exception:", dockerFile.getAbsolutePath()), e);
            return false;
        }

        return migratedSuccessfully;
    }

    /**
     *
     * @param dockerFile the file to migrate find-exec docker instruction(s)
     * @return boolean denoting if the dockerfile has had at least one find-exec instruction migrated
     */
    protected boolean migrateFindExecInstructions(File dockerFile) {
        if (dockerFile == null || !dockerFile.exists()) {
            return false;
        }

        boolean modified = false;

        try {
            Path path = dockerFile.toPath();
            Charset charset = StandardCharsets.UTF_8;
            List<String> resultLines = new ArrayList<>();

            Pattern pattern = Pattern.compile(DockerPipInstallMigration.findExecPattern);
            Matcher lineMatcher;
            for (String line : Files.readAllLines(path, charset)) {
                // check each line for a regex match
                lineMatcher = pattern.matcher(line);
                String result = line;
                if (lineMatcher.find()) {
                    // if match found, rebuild the line with relevant capture groups replaced
                    result = RUN_WITH_SET_E
                            + FILES_FIND
                            + lineMatcher.group(3)
                            + FOR_FILE_IN_FILES_DO
                            + lineMatcher.group(5)
                            + FILE_OR_EXIT_1_DONE;
                    modified = true;
                }
                resultLines.add(result);
            }
            if (modified) {
                Files.write(path, resultLines, charset);
            }
        } catch (IOException e) {
            return false;
        }
        return modified;
    }
}
