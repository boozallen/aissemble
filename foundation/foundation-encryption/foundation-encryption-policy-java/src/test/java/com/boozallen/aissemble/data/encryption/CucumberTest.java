package com.boozallen.aissemble.data.encryption;

/*-
 * #%L
 * aiSSEMBLE Data Encryption::Policy::Java
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
 * {@link CucumberTest} boot straps the cucumber steps.
 * 
 * @author Booz Allen Hamilton
 *
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/specifications", plugin = {
        "json:target/cucumber-reports/cucumber.json" }, tags = "not @manual")
public class CucumberTest {

}
