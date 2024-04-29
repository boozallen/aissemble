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

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FileUtils {

    /**
     *
     * @param file the File
     * @param regex a regex representing the text to replace, as a String
     * @param replacement the replacement text to substitute the regex
     * @return An ArrayList of Strings representing each capture group in the regex that was matched
     */
    public static boolean replaceInFile(File file, String regex, String replacement) throws IOException {
        boolean replacedInFile = false;
        if (file != null && file.exists()) {
            Charset charset = StandardCharsets.UTF_8;
            String fileContent = new String(Files.readAllBytes(file.toPath()), charset);

            Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(fileContent);
            String newFileContent = matcher.replaceAll(replacement);
            IOUtils.write(newFileContent, new FileOutputStream(file), charset);
            replacedInFile = true;
        }
        return replacedInFile;
    }

    /**
     * Function to read in the {@link File} object and return a {@link List} of the contents.
     * @param file {@link File} to read
     * @return {@link List} of the contents
     * @throws IOException
     */
    public static List<String> readAllFileLines(File file) throws IOException {
		return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
    }

    /**
     * Function to write a {@link List} of the contents to the {@link File} object.
     * @param file {@link File} to write
     * @param contents {@link List} of the contents
     * @throws IOException
     */
    public static void writeFile(File file, List<String> contents) throws IOException {
		Files.write(file.toPath(), contents, StandardCharsets.UTF_8);
    }

    /**
     * @see FileUtils#getRegExCaptureGroups(String, String)
     * @param regex a regex containing capture groups, as a String
     * @param file the file to search for matching capture groups
     * @return An ArrayList of Strings representing each capture group in the regex that was matched
     */
    public static ArrayList<String> getRegExCaptureGroups(String regex, File file) throws IOException {
        String fileContent = "";
        if (file != null && file.exists()) {
            Charset charset = StandardCharsets.UTF_8;
            fileContent = new String(Files.readAllBytes(file.toPath()), charset);
        }
        return StringUtils.isNotEmpty(fileContent) ? getRegExCaptureGroups(regex, fileContent) : new ArrayList<>();
    }

    /**
     *
     * @param regex a regex containing capture groups, as a String
     * @param input the string to search for matching capture groups
     * @return An ArrayList of Strings representing each capture group in the regex that was matched
     */
    public static ArrayList<String> getRegExCaptureGroups(String regex, String input) {
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(input);

        ArrayList<String> captured = new ArrayList<>();
        if (matcher.find()) {
            // Skip the 0 index -- the first match is always all capture groups put together
            for (int i = 1; i <= matcher.groupCount(); ++i) {
                captured.add(matcher.group(i));
            }
        }

        return captured;
    }

    /**
     * Infers the indentation style from the given line.
     *
     * @param line the line to infer the indentation style from
     * @param level the level of indentation of the line
     * @return a single indent in the inferred style
     */
    public static String getIndent(String line, int level) {
        int i = 0;
        while (i < line.length() && Character.isWhitespace(line.charAt(i))) {
            i++;
        }
        return line.substring(0, i/level);
    }
}
