package com.boozallen.aissemble.maven.enforcer.helper;

/*-
 * #%L
 * aiSSEMBLE::Support::Enforcer
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public abstract class BaseHelper {
    private static final Logger logger = LoggerFactory.getLogger(BaseHelper.class);
    protected String baseCommand;
    protected final File workingDirectory;

    protected BaseHelper(File workingDirectory, String baseCommand) {
        this.workingDirectory = workingDirectory;
        this.baseCommand = baseCommand;
    }

    /**
     * Executes a command with the given arguments, logs the executed command
     * at DEBUG level, and returns the resultant process output as a string.
     *
     * @param arguments argument to be used in the command
     * @return String value of the command results
     * @throws ShellExecutionException
     */
    public String quietlyExecute(List<String> arguments) throws ShellExecutionException {
        return execute(arguments, Level.DEBUG);
    }

    /**
     * Executes a command with the given arguments, logs the executed command
     * at INFO level, and returns the resultant process output as a string.
     *
     * @param arguments argument to be used in the command
     * @return String value of the command results
     * @throws ShellExecutionException
     */
    public String execute(List<String> arguments) throws ShellExecutionException {
        return execute(arguments, Level.INFO);
    }

    /**
     * Executes a command with the given arguments, logs the executed
     * command, and returns the resultant process output as a string.
     *
     * @param arguments argument to be used in the command
     * @param logLevel logging level
     * @return String value of the command results
     * @throws ShellExecutionException
     */
    public String execute(List<String> arguments, Level logLevel) throws ShellExecutionException {
        ProcessExecutor executor = createExecutor(arguments);

        if (logger.isInfoEnabled() || logger.isDebugEnabled()) {
            String logStatement = String.format("Executing command: %s %s", baseCommand, StringUtils.join(arguments, " "));
            if (Level.INFO.equals(logLevel)) {
                logger.info(logStatement);
            } else if (Level.DEBUG.equals(logLevel)) {
                logger.debug(logStatement);
            }
        }
        return executor.executeAndGetResult(logger);
    }

    public ProcessExecutor createExecutor(List<String> arguments) {
        List<String> fullCommandArgs = new ArrayList<>();
        fullCommandArgs.add(baseCommand);
        fullCommandArgs.addAll(arguments);
        return new ProcessExecutor(workingDirectory, fullCommandArgs, Platform.guess(), null);
    }
}
