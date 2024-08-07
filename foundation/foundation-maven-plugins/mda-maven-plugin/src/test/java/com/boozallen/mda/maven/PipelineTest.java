package com.boozallen.mda.maven;

/*-
 * #%L
 * MDA Maven::Plugin
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.mda.maven.mojo.PipelineArtifactsMojo;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static junit.framework.TestCase.*;

/**
 * Implementation steps for Pipeline.feature
 */
public class PipelineTest {
    public static final Logger logger = LoggerFactory.getLogger(PipelineTest.class);
    private MojoTestCaseWrapper mojoTestCase = new MojoTestCaseWrapper();
    private File modelSource;
    private String testProject;
    private String implementationType;
    private PipelineArtifactsMojo mojo;

    // Mojo Configuration Parameters
    private String outputDirectory;
    private String pipelinesDirectory;
    private String targetPipelineType;
    private List<String> targetArtifactTypes;
    private String dataRecordsDirectory;
    private List<String> dataRecordModules;

    @Before("@Pipeline")
    public void configureMavenTestSession() throws Exception {
        mojoTestCase.configurePluginTestHarness();
    }

    @After("@Pipeline")
    public void tearDownMavenPluginTestHarness() throws Exception {
        mojoTestCase.tearDownPluginTestHarness();
    }

    @Before("@CopyArtifacts")
    public void createTestProject() throws IOException {
        Path testPom = Paths.get("src", "test", "resources", "test-pom", "pom.xml").toAbsolutePath();
        Path testProject = Paths.get("target", "test-project").toAbsolutePath();
        Files.createDirectories(testProject);
        Files.copy(testPom, testProject.resolve("pom.xml"));
        this.testProject = testProject.toString();
    }

    @After("@CopyArtifacts")
    public void teardownTestProject() throws IOException {
        // Clear the contents of the output directory after each test run.  We still need the maven-clean-plugin
        // execution as well for redundancy, and ensuring that we start out with a clean slate.
        FileUtils.deleteDirectory(Paths.get(this.testProject).toFile());
    }

    @Given("a {string} pipeline model")
    public void a_type_pipeline_model(String implementation) {
        //verify the model artifact is present for the type
        this.modelSource = new File("src/test/resources/models/models.jar");

        assertNotNull(this.modelSource);
        assertTrue(this.modelSource.exists());

        //save the implementation type for validation later
        this.implementationType = implementation;
    }

    @Given("a target output directory")
    public void a_target_output_directory() {
        //set the output directory to the one used in the test pom
        this.outputDirectory = Paths.get(testProject,"target", "dockerbuild").toAbsolutePath().toString();
    }

    @Given("the plugin is configured to target {string} pipelines with {string} artifacts")
    public void the_plugin_is_configured_to_target_type_pipelines(String type, String artifacts) {
        this.targetPipelineType = type;
        this.targetArtifactTypes = "DEFAULT".equals(artifacts) ? null : List.of(artifacts.split(","));

        //set the custom resource location for the pipeline files
        this.pipelinesDirectory = new File("src/test/resources/pipelines/" + type).getAbsolutePath() + "/";
    }

    @Given("a data records directory {string}")
    public void a_list_of_data_record_modules(String directory) {
        this.dataRecordsDirectory = directory;
    }

    @Given("a list of data record modules:")
    public void a_list_of_data_record_modules(List<String> modules) {
        this.dataRecordModules = modules;
    }

    @Given("a dictionary and 0 or more record models")
    public void a_dictionary_and_record_models() {
        this.modelSource = new File("src/test/resources/models/models-with-records.jar");
    }

    @When("the copy-pipeline-artifacts goal is run")
    public void the_copy_pipeline_artifacts_goal_is_run() {
        //Read in the test pom  for the correct pipeline type and configure the mojo with the parameters
        File testPom = new File(testProject + "/pom.xml");

        try {
            mojo = (PipelineArtifactsMojo) mojoTestCase.lookupConfiguredMojo(testPom, "copy-pipeline-artifacts");
            mojo.setModelsSource(modelSource);
            mojo.setPipelinesDirectory(pipelinesDirectory);
            if(StringUtils.isNotBlank(outputDirectory)){
                mojo.setOutputDirectory(outputDirectory);
            }
            if(StringUtils.isNotBlank(targetPipelineType)){
                mojo.setTargetPipelineTypes(List.of(targetPipelineType));
            }
            if(targetArtifactTypes != null){
                mojo.setTargetArtifactTypes(targetArtifactTypes);
            }
            if(dataRecordsDirectory != null){
                mojo.setDataRecordsDirectory(dataRecordsDirectory);
            }
            if(dataRecordModules != null){
                mojo.setDataRecordModules(dataRecordModules);
            }
            mojo.execute();
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure the Pipeline Artifacts Mojo and read in the pipeline models", e);
        }
    }

    @Then("the pipeline model is read")
    public void the_pipeline_model_is_read() {
        //no-op due to no easy way to access pipeline objects
    }

    @Then("the {string} file\\(s) from the pipeline will be moved to the target directory")
    public void the_file_s_from_each_pipeline_will_be_moved_to_the_target_directory(String fileTypes) throws IOException {
        String[] fileTypesList = fileTypes.split(",");
        String pipelineName = this.implementationType + "-pipeline";
        Path sourceDir = Paths.get(this.pipelinesDirectory, pipelineName);
        IncorrectFiles collector = new IncorrectFiles();
        checkAllFileTypes(fileTypesList, sourceDir, pipelineName, collector);
        assertEquals("Some pipeline files do not exist.", Collections.emptyList(),
                collector.getMissingFiles());
        assertEquals("Some pipeline files do not match source files.", Collections.emptyList(),
                collector.getMismatchedFiles());
    }

    @Then("the {string} file\\(s) from each data record module will be moved to the target directory")
    public void the_file_s_from_each_data_record_module_will_be_moved_to_the_target_directory(String fileTypes) throws IOException {
        assertDataRecordModulesCopied(fileTypes, this.dataRecordModules);
    }

    @Then("the {string} file\\(s) from {string} module will be moved to the target directory")
    public void the_file_s_from_module_will_be_moved_to_the_target_directory(String fileTypes, String module) throws IOException {
        assertDataRecordModulesCopied(fileTypes, List.of(module));
    }

    private void assertDataRecordModulesCopied(String fileTypes, List<String> modules) throws IOException {
        String[] fileTypesList = fileTypes.split(",");
        IncorrectFiles collector = new IncorrectFiles();

        for (String modulePath : modules) {
            Path sourceDir = Paths.get(this.dataRecordsDirectory, modulePath);
            String moduleName = sourceDir.getFileName().toString();
            checkAllFileTypes(fileTypesList, sourceDir, moduleName, collector);
        }

        assertEquals("Some data files do not exist.", Collections.emptyList(),
                collector.getMissingFiles());
        assertEquals("Some data files do not match source files.", Collections.emptyList(),
                collector.getMismatchedFiles());
    }

    private void checkAllFileTypes(String[] fileTypesList, Path sourceDir, String moduleName, IncorrectFiles collector) throws IOException {
        Path baseTargetDir = Paths.get(outputDirectory);
        Path pythonCodeTarget =  baseTargetDir.resolve(moduleName);
        Path wheelTarget =  baseTargetDir.resolve("wheels");
        Path requirementsTarget = baseTargetDir.resolve("requirements").resolve(moduleName);

        for (String fileType : fileTypesList) {
            Path targetDir; // The destination for files depends on the type of file being copied.
            if (".yaml".equalsIgnoreCase(fileType) || ".jar".equalsIgnoreCase(fileType)) {
                targetDir = baseTargetDir;
            } else if (".tar.gz".equalsIgnoreCase(fileType) || ".py".equalsIgnoreCase(fileType)) {
                targetDir = pythonCodeTarget;
            } else if (".whl".equalsIgnoreCase(fileType)) {
                targetDir = wheelTarget;
            } else if (".txt".equalsIgnoreCase(fileType)) {
                targetDir = requirementsTarget;
            } else {
                throw new RuntimeException("Unsupported file type: " + fileType);
            }

            if (!".yaml".equalsIgnoreCase(fileType)) {
                //confirm the original files have been copied over to the target directory
                checkFilesExistAndMatch(sourceDir, targetDir, fileType, collector);
            } else {
                // Original values file is inside an archive and won't exist
                checkFilesExist(targetDir, fileType, collector);
            }
        }
    }

    /**
     * Asserts that all files in the source directory with the given extension have been copied exactly into the target
     * directory, without enforcing that the directory structure is the same. For instance, if the source directory has
     * a file at "a/b/file1.txt" and the target directory has a file at "c/file1.txt", this method will pass.
     *
     * @param sourceDir The source directory from which files were copied
     * @param targetDir The target directory to which files were copied
     * @param fileType The file type to check for
     * @throws IOException If an error occurs while collecting/comparing the files
     */
    private void checkFilesExistAndMatch(Path sourceDir, Path targetDir, String fileType, IncorrectFiles collector) throws IOException {
        // Ensure the target directory exists, so we get the list of missing files even if nothing was copied
        Files.createDirectories(targetDir);
        Map<String, File> originalFiles = getFilesByName(sourceDir, fileType);
        Map<String, File> copiedFiles = getFilesByName(targetDir, fileType);

        for (String fileName : originalFiles.keySet()) {
            logger.trace("Checking for file [{}] under [{}]", fileName, sourceDir);
            String originalPath = sourceDir.getFileName() + "/" + originalFiles.get(fileName).toPath();
            if (!copiedFiles.containsKey(fileName)) {
                collector.addMissingFile(originalPath);
            } else if (!FileUtils.contentEquals(originalFiles.get(fileName), copiedFiles.get(fileName))) {
                collector.addMismatchedFile(originalPath);
            }
        }
    }

    private void checkFilesExist(Path targetDir, String fileType, IncorrectFiles collector) throws IOException {
        Optional<Path> copiedFile = Files.walk(targetDir)
                .filter(Files::isRegularFile)
                .filter(file -> file.getFileName().toString().endsWith(fileType))
                .findAny();
        if (copiedFile.isEmpty() || !Files.exists(copiedFile.get())) {
            collector.addMissingFile(fileType);
        }
    }

    /**
     * Returns a map of file names to files for all files in the given directory that end with the given extension.
     *
     * @param directory The directory to search for files
     * @param fileType  The file type to filter on
     * @return A map of file names to files
     * @throws IOException If an error occurs while collecting the files
     */
    private Map<String, File> getFilesByName(Path directory, String fileType) throws IOException {
        return Files.walk(directory)
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .filter(file -> file.toString().endsWith(fileType))
                .collect(Collectors.toMap(Path::toString, Path::toFile));
    }

    private static class IncorrectFiles {
        private final List<String> missingFiles = new ArrayList<>();
        private final List<String> mismatchedFiles = new ArrayList<>();

        public void addMissingFile(String fileName) {
            missingFiles.add(fileName);
        }

        public void addMismatchedFile(String fileName) {
            mismatchedFiles.add(fileName);
        }

        public List<String> getMissingFiles() {
            return missingFiles;
        }

        public List<String> getMismatchedFiles() {
            return mismatchedFiles;
        }
    }
}
