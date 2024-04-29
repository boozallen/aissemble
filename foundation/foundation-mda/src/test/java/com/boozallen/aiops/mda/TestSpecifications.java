package com.boozallen.aiops.mda;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Test harness to run Gherkin Business Driven Development specifications.
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/specifications", plugin = "json:target/cucumber-reports/cucumber.json", tags="not @manual")
public class TestSpecifications {
    
}
