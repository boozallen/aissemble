package com.boozallen.aissemble.data.encryption;

/*-
 * #%L
 * aiSSEMBLE Data Encryption::Encryption (Java)
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        glue = "com.boozallen.aissemble.data.encryption",
        features = "src/test/resources/specifications",
        plugin = {"json:target/cucumber-reports/cucumber.json"},
        tags = "not @manual and not @integration")
public class CucumberTest {

}
