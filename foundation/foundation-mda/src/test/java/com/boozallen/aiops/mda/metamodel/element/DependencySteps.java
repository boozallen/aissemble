package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
