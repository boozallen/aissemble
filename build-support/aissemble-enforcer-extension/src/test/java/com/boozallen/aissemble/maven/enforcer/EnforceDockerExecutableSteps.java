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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.maven.enforcer.rule.api.EnforcerLogger;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;

import com.boozallen.aissemble.maven.enforcer.testenforcers.EnforceDockerExecutableTest;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class EnforceDockerExecutableSteps {

    private static final Path TEST_FILE_FOLDER = Paths.get("target", "test-classes", "test-files");
    private static final Path TEST_DEFAULT_DOCKER_URL = TEST_FILE_FOLDER.resolve("default");
    private final String defaultDockerContextUrl = TEST_DEFAULT_DOCKER_URL.resolve("docker.sock").toString();
    private String dockerHost = null;
    private String dockerContext = null;
    private String operatingSystem = null;
    private String dockerContextUrl = null;
    private EnforcerRuleException enforcerRuleException = null;
    private boolean isUnixPath;
    private boolean enabled;
    private EnforcerLogger enforcerLogger;

    @Before("@enforce-docker-executable")
    public void before() throws IOException {
        FileUtils.deleteDirectory(TEST_FILE_FOLDER.toFile());
        this.dockerHost = null;
        this.dockerContext = null;
        this.operatingSystem = "unix";
        this.dockerContextUrl = null;
        this.enforcerRuleException = null;
        this.isUnixPath = true;
        this.enabled = true;
        this.enforcerLogger = mock(EnforcerLogger.class);
    }

    @Given("the docker_host environment variable is {string}")
    public void theDocker_hostEnvironmentVariableIs(String present) {
        if (present.equals("present")) {
            this.dockerHost = TEST_DEFAULT_DOCKER_URL + "/docker.sock";
        }
    }

    @Given("the docker context is set to default")
    public void theDockerContextIsSetToDefault() {
        this.dockerContext = "default";
        this.dockerContextUrl = TEST_DEFAULT_DOCKER_URL + "/docker.sock";
    }

    @Given("the location is {string}")
    public void theLocationIs(String reachability) throws IOException {
        if (reachability.equals("reachable")) {
            new File(this.dockerHost).getParentFile().mkdirs();
            new File(this.dockerHost).createNewFile();
        }
    }

    @Given("the default location is {string}")
    public void theDefaultLocationIs(String reachability) throws IOException {
        if (reachability.equals("reachable")) {
            TEST_DEFAULT_DOCKER_URL.toFile().mkdirs();
            new File(defaultDockerContextUrl).createNewFile();
        }
    }

    @Given("the docker context value is {string}")
    public void theDockerContextValueIs(String dockerContext) {
        this.dockerContext = dockerContext;
        this.dockerContextUrl = TEST_FILE_FOLDER + "/" + dockerContext + "/docker.sock";
    }

    @Given("the detected operating system is {string}")
    public void theDetectedOperatingSystemIs(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    @Given("the docker context path does not have a unix schema")
    public void theDockerContextPathDoesNotHaveAUnixSchema() {
        this.isUnixPath = false;
    }

    @Given("the docker enforcer rule is disabled")
    public void theDockerEnforcerRuleIsDisabled() {
        this.enabled = false;
    }

    @When("the docker executable enforcer is called")
    public void theDockerExecutableEnforcerIsCalled() {
        EnforceDockerExecutableTest enforceDockerExecutableTest =
                new EnforceDockerExecutableTest(this.operatingSystem, this.dockerHost, this.dockerContext,
                        this.dockerContextUrl, this.defaultDockerContextUrl, this.isUnixPath);
        enforceDockerExecutableTest.setEnabled(this.enabled);
        enforceDockerExecutableTest.setLog(enforcerLogger);
        try {
            enforceDockerExecutableTest.execute();
        } catch (EnforcerRuleException e) {
            this.enforcerRuleException = e;
        }
    }

    @Then("the enforcer passes successfully")
    public void theEnforcerPassesSuccessfully() {
        assertNull(this.enforcerRuleException);
    }

    @Then("an error message is throw with {string}")
    public void anErrorMessageIsThrowWith(String errorMessagePattern) {
        assertNotNull(this.enforcerRuleException);
        Pattern pattern = Pattern.compile(errorMessagePattern);
        Matcher matcher = pattern.matcher(this.enforcerRuleException.getMessage());
        assertTrue("Incorrect error was thrown", matcher.find());
    }

    @Then("a warning is logged telling users the enforcer is skipped and to set DOCKER_HOST")
    public void aWarningIsLoggedTellingUsersTheEnforcerIsSkippedAndToSetDOCKER_HOST() {
        assertNull(this.enforcerRuleException);
    }
}
