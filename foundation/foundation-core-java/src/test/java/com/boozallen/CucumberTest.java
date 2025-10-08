package com.boozallen;

/*-
 * #%L
 * aiSSEMBLE Foundation::AIOps MDA Patterns::Spark
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
