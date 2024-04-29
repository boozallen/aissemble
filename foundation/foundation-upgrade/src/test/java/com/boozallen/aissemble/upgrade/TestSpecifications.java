package com.boozallen.aissemble.upgrade.migration;/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test harness to run Gherkin Business Driven Development specifications.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/specifications",
        plugin = {"json:target/cucumber-reports/cucumber.json"},
        tags = "not @manual")
public class TestSpecifications {

}
