package com.boozallen.aissemble.maven.enforcer.testenforcers;

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

import java.net.URI;

import com.boozallen.aissemble.maven.enforcer.EnforceDockerExecutable;

public class EnforceDockerExecutableTest extends EnforceDockerExecutable {
    private final String os;
    private final String dockerHost;
    private final String dockerContextName;
    private final String dockerContextUrl;
    private final String defaultDockerContextUrl;
    private final boolean isUnixPath;

    public EnforceDockerExecutableTest(String os, String dockerHost, String dockerContextName,
                                       String dockerContextUrl, String defaultDockerContextUrl, boolean isUnixPath) {
        this.os = os;
        this.dockerHost = dockerHost;
        this.dockerContextName = dockerContextName;
        this.dockerContextUrl = dockerContextUrl;
        this.defaultDockerContextUrl = defaultDockerContextUrl;
        this.isUnixPath = isUnixPath;
    }

    @Override
    protected boolean isUnix() {
        return os.equals("unix");
    }

    @Override
    protected String getDockerHost() {
        return this.dockerHost;
    }

    @Override
    protected String getDockerContextName() {
        return this.dockerContextName;
    }

    @Override
    protected String getDockerContextUrl(String contextName) {
        if (contextName.equalsIgnoreCase("default")) {
            return this.defaultDockerContextUrl;
        }
        return this.dockerContextUrl;
    }

    @Override
    protected boolean isUriNonUnix(URI uri) {
        return !isUnixPath;
    }
}
