package com.boozallen.aissemble.upgrade.migration.version_specific;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
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

import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertFalse;

public class ArgoCDRemovalMigrationStep extends AbstractMigrationTest {

    @Given("a project has yaml file under templates directory with following properties:")
    public void AProjectHasYamlFileUnderTemplatesDirectoriesWith(String properties) throws IOException {
        setTestFileToVersionMigration("ArgoCDRemovalMigration", "templates/argo-application.yaml");
        BufferedWriter writer = new BufferedWriter(new FileWriter(testFile.getAbsoluteFile()));
        writer.write(properties);
        writer.close();
    }

    @When("the ArgoCD removal migration is performed")
    public void theArgoCDRemovalMigrationIsPerformed() {
        ArgoCDRemovalMigration migration = new ArgoCDRemovalMigration();
        performMigration(migration);
    }

    @Then("the ArgoCD yaml is removed")
    public void theArgoCDYamlIsRemoved() {
        assertFalse("Failed to remove ArgoCD yaml file", testFile.exists());
    }

    @Then("the following files are removed in the parent directory:")
    public void theFollowingFilesAreRemovedInTheParentDirectory(List<String> files) {
        for (String file: files) {
            assertFalse(String.format("Failed to remove ArgoCD %s file", file), Files.exists(testFile.toPath().getParent().getParent().resolve(file)));
        }
    }

}
