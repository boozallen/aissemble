package com.boozallen.aissemble.maven.enforcer.helper;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Maven::Enforcer
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
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helps determine the version of Helm that is available on the user's
 * {@code PATH}.
 */
public class HelmVersionHelper extends BaseHelper {

    private static final Logger logger = LoggerFactory.getLogger(HelmVersionHelper.class);

    private static final String HELM_COMMAND = "helm";
    private static final String EXTRACT_VERSION_REGEX = "Version:\"v(.*?)\"";

    public HelmVersionHelper(File workingDirectory) {
        super(workingDirectory, HELM_COMMAND);
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
}
