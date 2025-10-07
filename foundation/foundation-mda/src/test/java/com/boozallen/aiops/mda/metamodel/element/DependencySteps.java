package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
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

import com.boozallen.aiops.mda.generator.config.deployment.spark.SparkDependencyConfiguration;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.math.NumberUtils;

public class DependencySteps extends AbstractModelInstanceSteps {
    private SparkDependencyConfiguration sparkDependencyConfiguration;

    @Given("the properties file has been properly filtered")
    public void props_are_filtered() {}

    @When("the spark dependency configuration is initialized")
    public void configuration_is_loaded() {
        sparkDependencyConfiguration = SparkDependencyConfiguration.getInstance();
    }

    @Then("appropriate version information will be available")
    public void version_information_is_accessible() {
        assert(sparkDependencyConfiguration.getSparkVersion() != null);
        assert(!sparkDependencyConfiguration.getSparkVersion().contains("${"));
        assert(NumberUtils.isParsable(sparkDependencyConfiguration.getSparkVersion().replaceAll("\\.", "")));
    }
}
