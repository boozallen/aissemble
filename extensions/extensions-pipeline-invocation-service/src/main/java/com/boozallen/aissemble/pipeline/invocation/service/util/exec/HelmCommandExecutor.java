package com.boozallen.aissemble.pipeline.invocation.service.util.exec;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Pipeline Invocation Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.pipeline.invocation.service.ValuesFileRegistry;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Singleton;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for building and executing Helm commands
 *
 * TODO: Refactor to use Technology Brewery Commons once released: https://github.com/TechnologyBrewery/commons
 */

@Singleton
public class HelmCommandExecutor {

    private static final String HELM_COMMAND = "helm";
    private static final Logger logger = LoggerFactory.getLogger(HelmCommandExecutor.class);
    private final Path workingDirectory;

    /**
     * Required information regarding the aissemble ecosystem.  Autopopulated from pom filtering.  Not for human use.
     */
    @ConfigProperty(name = "version.aissemble")
    private String aissembleVersion;
    @ConfigProperty(name = "aissemble.helm.repo.url")
    private String aissembleHelmRepoUrl;
    @ConfigProperty(name = "aissemble.helm.repo.protocol")
    private String aissembleHelmRepoProtocol;

    /**
     * private constructor to retain singleton status
     *
     * @param workingDirectory Working directory.  Should be consistent and is not generally expected to be human-modified.
     *                         Set up to be modifiable for future-proofing.
     */
    private HelmCommandExecutor(
            @ConfigProperty(name = ValuesFileRegistry.WORKING_DIR_CONFIG_STRING, defaultValue = ValuesFileRegistry.DEFAULT_WORKING_DIR)
            String workingDirectory) {
        this.workingDirectory = Path.of(workingDirectory);
    }

    /**
     * Executes a Helm command with the given arguments, logs the executed
     * command, logs the stdout/stderr generated by the process, and returns the
     * process exit code. This method should be utilized when it is desirable to
     * immediately show all of the stdout/stderr produced by a Helm command for
     * diagnostic purposes.
     *
     * @param arguments
     * @return
     * @throws ShellExecutionException
     */
    public int executeAndLogOutput(List<String> arguments) throws ShellExecutionException {
        logger.error("Executing Helm command: {} {}", HELM_COMMAND, StringUtils.join(arguments, " "));

        ProcessExecutor executor = createHelmExecutor(arguments);
        return executor.executeAndRedirectOutput(logger);
    }

    private ProcessExecutor createHelmExecutor(List<String> arguments) {
        List<String> fullCommandArgs = new ArrayList<>();
        fullCommandArgs.add(HELM_COMMAND);
        fullCommandArgs.addAll(arguments);
        return new ProcessExecutor(workingDirectory.toFile(), fullCommandArgs, Platform.guess(), null);
    }

    /**
     * Builds and executes the command to uninstall a release from the cluster if needed.  In the event that the release
     * is not present, fails silently as the result would be a no-op.
     *
     * @param appName Release name to uninstall from the cluster.
     */
    public void uninstallReleaseIfPresent(String appName) {
        try {
            executeAndLogOutput(List.of("uninstall", appName));
        } catch(ShellExecutionException ignored) {}
    }

    /**
     * Creates a base set of helm installation args including the install command, repo, resultant appName, and version.
     *
     * @param appName
     * @return Mutable collection of basic helm install arguments.
     */
    public List<String> createBaseHelmInstallArgs(String appName) {
        List<String> args = new ArrayList<>();

        args.add("install");
        args.add(appName);
        args.add(aissembleHelmRepoProtocol + "://" + aissembleHelmRepoUrl + "/aissemble-spark-application-chart");
        args.add("--version");
        args.add(aissembleVersion);
        return args;
    }
}
