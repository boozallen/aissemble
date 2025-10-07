package com.boozallen.aissemble.maven.enforcer;

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
import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rules.AbstractStandardEnforcerRule;
import org.apache.maven.project.MavenProject;

import com.boozallen.aissemble.maven.enforcer.helper.DockerExecutableHelper;

@Named("enforceDockerExecutable")
public class EnforceDockerExecutable extends AbstractStandardEnforcerRule {
    private static final String DOCKER_HOST = "DOCKER_HOST";
    private static final String DEFAULT = "default";

    @Inject
    private MavenProject project;

    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Checks if docker is executable in current environment. Will skip if the OS is not UNIX-based and if the docker
     * host url is not UNIX-based
     *
     * @throws EnforcerRuleException if the above steps do not pass
     */
    @Override
    public void execute() throws EnforcerRuleException {
        if (isEnabled()) {
            doExecute();
        }
    }

    private void doExecute() throws EnforcerRuleException {
        // Rule only applicable to UNIX-based OS. Log warn and pass if OS is outside scope
        if (!isUnix()) {
            getLog().warn("OS is not UNIX-based. Skipping enforcement of docker executable");
            return;
        }

        String dockerHost;
        URI dockerContextUri;
        try {
            // Check if DOCKER_HOST is present and valid
            dockerHost = getDockerHost();
            if (dockerHost != null) {
                validateDockerHost(dockerHost);
                return;
            }

            // If no DOCKER_HOST then check the default docker context
            dockerContextUri = new URI(getDockerContextUrl(DEFAULT));
            if (isUriNonUnix(dockerContextUri)) {
                getLog().warn("Docker host schema is not UNIX. Skipping enforcement of docker executable");
                return;
            }
        } catch (URISyntaxException e) {
            String message = String.format("%s\nCannot connect to the Docker daemon at current Docker context path. " +
                    "Is the docker daemon running?", this.getMessage());
            // If the URI in DOCKER_HOST or docker context is malformatted
            throw new EnforcerRuleException(message);
        }

        if (new File(dockerContextUri.getPath()).exists()) {
            // if the default location is reachable, then return successfully
            return;
        } else if (StringUtils.equals(DEFAULT, getDockerContextName())) {
            // if the default location is not reachable and the docker context is default, fail because the docker
            // host will not be reachable
            String message = String.format("%s\nCannot connect to the Docker daemon at %s. Is the docker " +
                    "daemon running?", this.getMessage(), dockerContextUri);
            throw new EnforcerRuleException(message);
        }

        // If the context is not default, the default location is not reachable, and DOCKER_HOST is not set to
        // the current context, then the docker build will fail.
        // Throw a helpful message to users to set the DOCKER_HOST to the current context
        try {
            dockerContextUri = new URI(getDockerContextUrl(getDockerContextName()));
            String message = String.format("""
                    %s
                    Environment variable DOCKER_HOST needs to be set to the current Docker context host url
                        export DOCKER_HOST=%s
                    """, this.getMessage(), dockerContextUri);
            throw new EnforcerRuleException(message);
        } catch (URISyntaxException e) {
            String message = String.format("%s\nCannot connect to the Docker daemon at current Docker context path. " +
                    "Is the docker daemon running?", this.getMessage());
            throw new EnforcerRuleException(message);
        }
    }

    private void validateDockerHost(String dockerHost) throws EnforcerRuleException, URISyntaxException {
        URI dockerHostUri = new URI(dockerHost);
        if (isUriNonUnix(dockerHostUri)) {
            // Rule does not validate external Docker hosts
            getLog().warn("Docker host schema is not UNIX. Skipping enforcement of docker executable");
        } else {
            // If present but not reachable, then fail
            if (!new File(dockerHostUri.getPath()).exists()) {
                String message = String.format("%s\nCannot connect to the Docker daemon at the DOCKER_HOST " +
                        "environment variable location %s. Is the docker daemon running?", this.getMessage(), dockerHost);
                throw new EnforcerRuleException(message);
            }
        }
    }

    protected boolean isUnix() {
        return SystemUtils.IS_OS_UNIX;
    }

    protected String getDockerHost() {
        return System.getenv(DOCKER_HOST);
    }

    protected String getDockerContextName() {
        DockerExecutableHelper dockerExecutableHelper = new DockerExecutableHelper(project.getBasedir());
        return dockerExecutableHelper.getDockerContextName();
    }

    protected String getDockerContextUrl(String contextName) {
        DockerExecutableHelper dockerExecutableHelper = new DockerExecutableHelper(project.getBasedir());
        return dockerExecutableHelper.getDockerContextUrl(contextName);
    }

    protected boolean isUriNonUnix(URI uri) {
        if (uri != null && uri.getScheme() != null) {
            return !uri.getScheme().startsWith("unix");
        }
        return true;
    }
}
