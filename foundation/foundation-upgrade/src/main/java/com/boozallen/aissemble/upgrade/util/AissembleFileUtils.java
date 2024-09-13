package com.boozallen.aissemble.upgrade.util;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AissembleFileUtils {

    /**
     * Copy lines between startLine and endLine, inclusive, from the file.
     *
     * @param file the file to copy lines from
     * @param startLine the index of the first line to copy
     * @param endLine the index of the last line to copy
     * @return a list of the copied lines
     */
    public static List<String> getLines(Path file, int startLine, int endLine) {
        try (Stream<String> lines = Files.lines(file)) {
            return lines.skip(startLine)
                    .limit(endLine - startLine + 1)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
