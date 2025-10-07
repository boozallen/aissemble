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
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class DockerExecutableHelper extends BaseHelper {
    private static final String DOCKER_COMMAND = "docker";
    private static final String CONTEXT_HOST_FORMAT = "{{.Endpoints.docker.Host}}";

    public DockerExecutableHelper(File workingDirectory) {
        super(workingDirectory, DOCKER_COMMAND);
    }

    /**
     * Gets the current docker context name and host url
     *
     * @return A string map with name, host url
     */
    public String getDockerContextName() {
        List<String> command = Arrays.asList("context", "show");
        return quietlyExecute(command);
    }

    public String getDockerContextUrl(String contextName) {
        if (StringUtils.isNotEmpty(contextName)) {
            List<String> command = Arrays.asList("context", "inspect", contextName, "--format", CONTEXT_HOST_FORMAT);
            return quietlyExecute(command);
        }
        return null;
    }
}
