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
import com.boozallen.aissemble.upgrade.migration.extensions.HabushuMonorepoDependencyMigrationTest;
import com.boozallen.aissemble.upgrade.migration.utils.MigrationTestUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;

public class HabushuContainerizationSteps extends AbstractMigrationTest {
    HabushuMonorepoDependencyMigrationTest habushuMonorepoDependencyMigration = new HabushuMonorepoDependencyMigrationTest();
    MlTrainPipelineDockerMigration trainingDockerPomMigration = new MlTrainPipelineDockerMigration();
    InferenceDockerPomMigration inferenceDockerPomMigration = new InferenceDockerPomMigration();
    SparkWorkerContainerizationPomMigration sparkWorkerContainerizationPomMigration = new SparkWorkerContainerizationPomMigration();

    @Given("a POM with packaging habushu")
    public void aPomWithPackagingHabushu() throws IOException, XmlPullParserException {
        setTestFileToVersionMigration("HabushuMonorepoDependencyMigration", "pom.xml");
        habushuMonorepoDependencyMigration.setMavenProject(MigrationTestUtils.createMavenProjectFromPom(testFile));
    }

    @Given("a pom-type dependency on another habushu-packaged module that is in the list of Maven projects")
    public void aPomTypeDependencyOnAnotherHabushuPackagedModuleThatIsInTheListOfMavenProjects() {
        //dependencies are set in the test POM file, so just add project
        habushuMonorepoDependencyMigration.addProject("habushu-library", "habushu");
    }

    @Given("an training POM without a Habushu containerize goal")
    public void anTrainingPOMWithoutAHabushuContainerizeGoal() throws IOException, XmlPullParserException {
        setTestFileToVersionMigration("MlTrainPipelineDockerMigration", "pom.xml");
        trainingDockerPomMigration.setMavenProject(MigrationTestUtils.createMavenProjectFromPom(testFile));
    }

    @Given("an training POM with a Habushu containerize goal")
    public void anTrainingPOMWithAHabushuContainerizeGoal() throws XmlPullParserException, IOException {
        setTestFileToVersionMigration("MlTrainPipelineDockerMigration", "pom-with-containerize.xml");
        trainingDockerPomMigration.setMavenProject(MigrationTestUtils.createMavenProjectFromPom(testFile));
    }

    @Given("the fermenter-mda plugin has the profile aissemble-training-docker")
    public void theFermenterMdaPluginHasTheProfileAissembleTrainingDocker(){
        // Already covered by test POM file content
    }

    @Given("an inference POM without a Habushu containerize goal")
    public void anInferencePOMWithoutAHabushuContainerizeGoal() throws IOException, XmlPullParserException {
        setTestFileToVersionMigration("InferenceDockerPomMigration", "pom.xml");
        inferenceDockerPomMigration.setMavenProject(MigrationTestUtils.createMavenProjectFromPom(testFile));
    }

    @Given("an inference POM with a Habushu containerize goal")
    public void anInferencePOMWithAHabushuContainerizeGoal() throws XmlPullParserException, IOException {
        setTestFileToVersionMigration("InferenceDockerPomMigration", "pom-with-containerize.xml");
        inferenceDockerPomMigration.setMavenProject(MigrationTestUtils.createMavenProjectFromPom(testFile));
    }

    @Given("the fermenter-mda plugin has the profile aissemble-inference-docker")
    public void theFermenterMdaPluginHasTheProfileAissembleInferenceDocker() {
        // Handled in test file
    }

    @Given("a non ML Inference docker POM files")
    public void aNonMLInferenceDockerPOMFiles() throws XmlPullParserException, IOException {
        setTestFileToVersionMigration("InferenceDockerPomMigration", "non-ml-inference-pom.xml");
        inferenceDockerPomMigration.setMavenProject(MigrationTestUtils.createMavenProjectFromPom(testFile));
    }

    @Given("a spark worker POM with a Habushu containerize goal")
    public void a_spark_worker_pom_with_a_habushu_containerize_goal() throws XmlPullParserException, IOException {
        setTestFileToVersionMigration("SparkWorkerContainerizationPomMigration", "with-habushu-containerize-plugin-pom.xml");
        sparkWorkerContainerizationPomMigration.setMavenProject(MigrationTestUtils.createMavenProjectFromPom(testFile));
    }

    @Given("the fermenter-mda plugin has the profile aissemble-spark-worker-docker")
    public void the_fermenter_mda_plugin_has_the_profile_aissemble_spark_worker_docker() {
        // Already covered by test POM file content
    }

    @Given("a spark worker POM without a Habushu containerize goal")
    public void a_spark_worker_pom_without_a_habushu_containerize_goal() throws XmlPullParserException, IOException {
        setTestFileToVersionMigration("SparkWorkerContainerizationPomMigration", "pom.xml");
        sparkWorkerContainerizationPomMigration.setMavenProject(MigrationTestUtils.createMavenProjectFromPom(testFile));
    }

    @Given("a spark worker POM without Habushu packaged dependencies")
    public void aSparkWorkerPOMWithoutHabushuPackagedDependencies() throws XmlPullParserException, IOException {
        setTestFileToVersionMigration("SparkWorkerContainerizationPomMigration", "without-habushu-packaged-dependencies.xml");
        sparkWorkerContainerizationPomMigration.setMavenProject(MigrationTestUtils.createMavenProjectFromPom(testFile));
    }

    @When("the training docker pom migration executes")
    public void theTrainingDockerPomMigrationExecutes() {
        performMigration(trainingDockerPomMigration);
        habushuMonorepoDependencyMigration.addProject("habushu-library", "habushu");
    }

    @When("the inference docker pom migration executes")
    public void theInferenceDockerPomMigrationExecutes() {
        performMigration(inferenceDockerPomMigration);
    }

    @When("the Habushu dependency type migration executes")
    public void theHabushuDependencyTypeMigrationExecutes() {
        performMigration(habushuMonorepoDependencyMigration);
    }

    @When("the spark worker docker pom migration executes")
    public void the_spark_worker_docker_pom_migration_executes() {
        performMigration(sparkWorkerContainerizationPomMigration);
    }

    @Then("the type of the monorepo dependency is updated to habushu")
    public void theTypeOfTheMonorepoDependencyIsUpdatedToHabushu() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("POM Dependency type not updated correctly");
    }

    @Then("the POM is updated to use the habushu containerize goal")
    public void thePOMIsUpdatedToUseTheHabushuContainerizeGoal() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("Habushu containerize-dependencies goal was not added to POM file");
    }

    @Then("the training docker pom migration was skipped")
    public void theTrainingDockerPomMigrationWasSkipped() {
        assertMigrationSkipped();
    }

    @Then("the inference docker pom migration was skipped")
    public void theInferenceDockerPomMigrationWasSkipped() {
        assertMigrationSkipped();
    }

    @Then("the spark worker docker pom migration was skipped")
    public void the_spark_worker_docker_pom_migration_was_skipped() {
        assertMigrationSkipped();
    }

    @Then("the spark worker docker POM is updated to use the habushu containerize goal")
    public void the_spark_worker_docker_pom_is_updated_to_use_the_habushu_containerize_goal() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("Spark Worker Docker POM Containerization did not match");
    }
}
