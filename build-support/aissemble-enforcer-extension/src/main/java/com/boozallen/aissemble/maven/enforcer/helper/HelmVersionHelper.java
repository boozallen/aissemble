package com.boozallen.aissemble.maven.enforcer.helper;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Maven::Enforcer
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * Helps determine the version of Helm that is available on the user's
 * {@code PATH}.
 */
public class HelmVersionHelper {

    private static final Logger logger = LoggerFactory.getLogger(HelmVersionHelper.class);

    private static final String HELM_COMMAND = "helm";
    private static final String EXTRACT_VERSION_REGEX = "\"v((\\d\\.?)+)\"";

    private final File workingDirectory;

    public HelmVersionHelper(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    /**
     * Retrieves the version of Helm that is set for the configured working
     * directory.
     *
     * @return
     */
    public String getCurrentHelmVersion() throws ShellExecutionException {
        String version = quietlyExecute(Collections.singletonList("version"));
        Pattern pattern = Pattern.compile(EXTRACT_VERSION_REGEX);
        Matcher matcher = pattern.matcher(version);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new ShellExecutionException("helm version could not be found");
    }

    /**
     * Executes a helm command with the given arguments, logs the executed command
     * at DEBUG level, and returns the resultant process output as a string.
     *
     * @param arguments
     * @return
     * @throws MojoExecutionException
     */
    protected String quietlyExecute(List<String> arguments) throws ShellExecutionException {
        return execute(arguments, Level.DEBUG);
    }

    /**
     * Executes a helm command with the given arguments, logs the executed command
     * at INFO level, and returns the resultant process output as a string.
     *
     * @param arguments
     * @return
     * @throws MojoExecutionException
     */
    protected String execute(List<String> arguments) throws ShellExecutionException {
        return execute(arguments, Level.INFO);
    }

    /**
     * Executes a helm command with the given arguments, logs the executed
     * command, and returns the resultant process output as a string.
     *
     * @param arguments
     * @return
     * @throws MojoExecutionException
     */
    private String execute(List<String> arguments, Level logLevel) throws ShellExecutionException {
        ProcessExecutor executor;

        executor = createHelmExecutor(arguments);

        if (logger.isInfoEnabled() || logger.isDebugEnabled()) {
            String logStatement = String.format("Executing command: %s %s", HELM_COMMAND, StringUtils.join(arguments, " "));
            if (Level.INFO.equals(logLevel)) {
                logger.info(logStatement);
            } else if (Level.DEBUG.equals(logLevel)) {
                logger.debug(logStatement);
            }
        }

        return executor.executeAndGetResult(logger);
    }

    private ProcessExecutor createHelmExecutor(List<String> arguments) {
        List<String> fullCommandArgs = new ArrayList<>();
        fullCommandArgs.add(HELM_COMMAND);
        fullCommandArgs.addAll(arguments);
        return new ProcessExecutor(workingDirectory, fullCommandArgs, Platform.guess(), null);
    }
}
