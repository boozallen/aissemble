package com.boozallen.aiops.mda.generator;/*-
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

import com.boozallen.aiops.mda.metamodel.element.*;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.fermenter.mda.GenerateSourcesHelper;
import org.technologybrewery.fermenter.mda.element.ExpandedProfile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SparkApplicationStep extends AbstractModelInstanceSteps {
    private static final Logger logger = LoggerFactory.getLogger(SparkApplicationStep.class);
    private boolean hasSparkPipeline;

    @Before("@spark-application-generation")
    public void setup(Scenario scenario) throws IOException {
        this.scenario = scenario.getName();
        FileUtils.deleteDirectory(GENERATED_METADATA_DIRECTORY);
        hasSparkPipeline = false;
    }

    @Given("a spark project named {string}")
    public void a_spark_project_named(String projectName) throws IOException {
        createProject(projectName, "shared");
    }

    @Given("a data-flow pipeline using data-delivery-spark")
    public void a_dataflow_pipeline_using_data_delivery_spark() throws IOException {
        a_pipeline_using_data_delivery("data-flow", "data-delivery-spark");
    }

    @Given("a dataflow pipeline using {string}")
    public void a_pipeline_using_data_delivery(String typeName, String implName) throws IOException {
        createAndSavePipeline("SparkPipeline", "data-flow", implName);
    }

    @Given("a file store named {string}")
    public void a_file_store_named(String fileStoreName) throws IOException {
        List<String> fileStoreNames = new ArrayList<>();
        fileStoreNames.add(fileStoreName);
        saveFileStores(createFileStores(fileStoreNames));
    }

    @Given("two file stores named {string} and {string}")
    public void two_file_stores_named_and(String fileStoreOneName, String fileStoreTwoName) throws IOException {
        List<String> fileStoreNames = new ArrayList<>();
        fileStoreNames.add(fileStoreOneName);
        fileStoreNames.add(fileStoreTwoName);
        saveFileStores(createFileStores(fileStoreNames));
    }

    @Given("a project has pyspark and {string} data models")
    public void a_pyspark_with_the_name_example(String other) throws Exception {
        createProject("example", "shared");
        addPysparkPipeline();
        if ("spark".equals(other)) {
            addSparkPipeline();
            hasSparkPipeline = true;
        }
    }

    @Given("the pipeline step uses data record with {string} profile")
    public void the_pipeline_steps_import_data_record_from(String dataRecordProfile) {
        addTestSharedPomFile(dataRecordProfile);
    }

    @When("the profile data-delivery-spark-pipeline is generated")
    public void the_profile_data_delivery_spark_pipeline_is_generated() throws Exception {
        readMetadata(projectName);
        generateSparkPipeline(loadProfiles());
    }

    @When("the pipeline is generated")
    public void the_profile_data_delivery_pyspark_pipeline_is_generated() throws Exception {
        readMetadata(projectName);
        Map<String, ExpandedProfile> profiles = loadProfiles();
        generatePysparkPipeline(profiles);

        if (hasSparkPipeline) {
            generateSparkPipeline(profiles);
        }
    }

    @Then("the {string}, {string}, and {string} configurations {string} generated")
    public void the_and_configurations_are_not_generated(String fsProvider, String fsAccessKeyId, String fsSecretAccessKey, String expectation) throws IOException {
        AtomicBoolean hasFsProviderEnvVar = new AtomicBoolean(false);
        AtomicBoolean hasFsAccessKeyIdEnvVar = new AtomicBoolean(false);
        AtomicBoolean hasFsSecretAccessKeyEnvVar = new AtomicBoolean(false);

        String pipelineSparkBaseValuesYamlFileName = "spark-pipeline-base-values.yaml";
        File pipelineSparkBaseValuesYamlFile = new File(projectDir.toString() + "/main/resources/apps/" + pipelineSparkBaseValuesYamlFileName);

        assertTrue("The " + pipelineSparkBaseValuesYamlFileName + " file was not generated!", pipelineSparkBaseValuesYamlFile.exists());

        Files.lines(Paths.get(pipelineSparkBaseValuesYamlFile.getAbsolutePath())).forEach(line -> {
            if(line.contains("- name: \"" + fsProvider + "\"")) {
                hasFsProviderEnvVar.set(true);
            }

            if(line.contains("- name: \"" + fsAccessKeyId + "\"")) {
                hasFsAccessKeyIdEnvVar.set(true);
            }

            if(line.contains("- name: \"" + fsSecretAccessKey + "\"")) {
                hasFsSecretAccessKeyEnvVar.set(true);
            }
        });

        assertEquals("File Store Env variable \"" + fsProvider + "\" found in " + pipelineSparkBaseValuesYamlFile, hasFsProviderEnvVar.get(), expectation.equals("are"));
        assertEquals("File Store Env variable \"" + fsAccessKeyId + "\" found in " + pipelineSparkBaseValuesYamlFile, hasFsAccessKeyIdEnvVar.get(), expectation.equals("are"));
        assertEquals("File Store Env variable \"" + fsAccessKeyId + "\" found in " + pipelineSparkBaseValuesYamlFile, hasFsSecretAccessKeyEnvVar.get(), expectation.equals("are"));
    }

    @Then("the pipeline steps are generated with correct {string} to import data")
    public void the_pipeline_steps_are_generated_with_correct_data_record_package_to_import_data(String dataRecordPackage) throws IOException {
        assertDataRecordPackageImport(dataRecordPackage);
    }

    /**
     * Create list of file store based on a list of file store names
     *
     * @param fileStoreNames the List of File Store Names
     */
    private List<FileStore> createFileStores(List<String> fileStoreNames) {
        List<FileStore> fileStores = new ArrayList<FileStore>();
        for (String fileStoreName : fileStoreNames) {
            FileStoreElement fileStoreElement = new FileStoreElement(fileStoreName);
            fileStores.add(fileStoreElement);
        }
        return fileStores;
    }

    protected void saveFileStores(List<FileStore> fileStores) throws IOException  {
        this.pipeline.setFileStores(fileStores);
        savePipelineToFile(pipeline);
    }

    private void addPipeline(boolean isPySparkPipeline) throws Exception {
        String pipelineName = isPySparkPipeline? "PysparkPipeline": "SparkPipeline";
        String impleName = isPySparkPipeline? "data-delivery-pyspark": "data-delivery-spark";
        String pipelineStepName = "exampleStep";
        createSampleDictionary();
        createSampleRecord();

        createAndSavePipeline(pipelineName, "data-flow", impleName, pipeline -> {
            try {
                StepElement pipelineStep = createPipelineNativeStepWithSampleRecord(pipelineStepName, "synchronous");
                pipeline.addStep(pipelineStep);
            } catch (Exception e) {
                logger.error("Failed to add step to test pipeline", e);
            }
        });
    }

    private void addPysparkPipeline() throws Exception {
        addPipeline(true);
    }

    private void addSparkPipeline() throws Exception {
        addPipeline(false);
    }

    private void generatePysparkPipeline(Map<String, ExpandedProfile> profiles) throws Exception {
        GenerateSourcesHelper.performSourceGeneration("data-delivery-pyspark-pipeline", profiles, this::createGenerationContext, (missingProfile, foundProfiles) -> {
            throw new RuntimeException("Missing profile: " + missingProfile);
        }, new Slf4jDelegate(logger), projectDir.toFile());
    }

    private void generateSparkPipeline(Map<String, ExpandedProfile> profiles) throws Exception {
        GenerateSourcesHelper.performSourceGeneration("data-delivery-spark-pipeline", profiles, this::createGenerationContext, (missingProfile, foundProfiles) -> {
            throw new RuntimeException("Missing profile: " + missingProfile);
        }, new Slf4jDelegate(logger), projectDir.toFile());
    }

    private void addTestSharedPomFile(String dataRecordProfile) {
        String content = """
                <project>
                      <build>
                        <plugins>
                            <plugin>
                                <groupId>org.technologybrewery.fermenter</groupId>
                                <artifactId>fermenter-mda</artifactId>
                                <inherited>false</inherited>
                                <executions>
                                    <execution>
                                        <id>generate-data-records</id>
                                        <phase>generate-sources</phase>
                                        <goals>
                                            <goal>generate-sources</goal>
                                        </goals>
                                        <configuration>
                                            <basePackage>com.boozallen</basePackage>
                                            <profile>%s</profile>
                                        </configuration>
                                    </execution>
                                </executions>
                            </plugin>
                        </plugins>
                    </build>
                </project>""";

        String directoryPath = this.projectDir.toAbsolutePath() + File.separator + this.projectDir.getFileName() + "-shared";
        String filePath = directoryPath + File.separator + "pom.xml";
        try {
            Path directory = Paths.get(directoryPath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            // Write to file
            Path file = Paths.get(filePath);
            Files.write(file, String.format(content, dataRecordProfile).getBytes());
        } catch (IOException e) {
            logger.error("Failed create the shared module pom file", e);
        }
    }

    private void assertDataRecordPackageImport(String dataRecordPackage) throws IOException {
        boolean foundImport = false;
        String importLine = String.format("from %s.record.test_record_with_field_list import TestRecordWithFieldList", dataRecordPackage);
        String generatedFile = File.separator + "generated" + File.separator + "step" + File.separator + "example_step_base.py";
        List<String> lines = Files.readAllLines(new File(this.projectDir.toAbsolutePath() + File.separator + generatedFile).toPath());
        for (String line: lines) {
            line = line.trim();
            if (line.equals(importLine)){
                foundImport = true;
                break;
            }
        }
        assertTrue("The generated step has incorrect data record import.", foundImport);
    }
}
