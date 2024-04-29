package com.boozallen;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA Patterns::Spark
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
 * Configures JUnit to pick up and run Cucumber tests.
 *
 * GENERATED STUB CODE - PLEASE ***DO*** MODIFY
 *
 * Originally generated from: templates/cucumber.surefire.harness.java.vm 
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/specifications",
        plugin = {"json:target/cucumber-reports/cucumber.json"},
        tags = "not @manual")
public class CucumberTest {

}
