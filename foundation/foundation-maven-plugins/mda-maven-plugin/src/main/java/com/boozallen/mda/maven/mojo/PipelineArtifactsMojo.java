package com.boozallen.mda.maven.mojo;

/*-
 * #%L
 * MDA Maven::Plugin
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.generator.common.PipelineImplementationEnum;
import com.boozallen.aiops.mda.generator.util.SemanticDataUtil;
import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import com.boozallen.mda.maven.ArtifactType;
import com.boozallen.mda.maven.PipelineType;
import com.boozallen.mda.maven.util.ArtifactCopier;
import com.boozallen.mda.maven.util.ArtifactCopierFactory;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.*;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceUrl;
import org.technologybrewery.fermenter.mda.metamodel.ModelRepositoryConfiguration;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

import com.boozallen.aiops.mda.metamodel.element.Pipeline;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.aether.resolution.ArtifactResolutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.boozallen.aiops.mda.generator.util.PipelineUtils.deriveArtifactIdFromCamelCase;
import static com.boozallen.aiops.mda.generator.util.PipelineUtils.deriveLowercaseSnakeCaseNameFromCamelCase;
import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * Mojo for reading in the MDA models from an artifact. Once the models are read in, the appropriate
 * maven plugin is called for collecting the necessary pipeline artifacts. For data-delivery-spark pipelines the
 * maven-dependency-plugin is used while for data-delivery-pyspark pipelines the maven-resources-plugin is used.
 */
@Mojo(name = "copy-pipeline-artifacts", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class PipelineArtifactsMojo extends AbstractMojo {
    private static final Logger logger = LoggerFactory.getLogger(PipelineArtifactsMojo.class);

    private File modelsSource;

    private ExecutionEnvironment environment;

    /**
     * Artifact id where the MDA models are stored, for example: {project-name}-pipeline-models
     */
    @Parameter(required = true)
    private String modelsArtifactId;

    @Parameter(required = true)
    private String groupId;

    /**
     * Directory used for finding python pipeline artifacts, for example: ${project.basedir}/{project-name}-pipelines/
     */
    @Parameter(required = true)
    private String pipelinesDirectory;

    @Parameter(required = true, defaultValue = "${project.build.directory}/")
    private String outputDirectory;

    @Parameter(defaultValue = "${project.build.directory}")
    private String projectBuildDirectory;

    /**
     * Which artifact types to retrieve as part of plugin execution.  See ArtifactType.java for valid options.
     */
    @Parameter(defaultValue = "TARBALL,JAR,REQUIREMENTS")
    private List<ArtifactType> targetArtifactTypes;

    /**
     * Configure the plugin to target only certain pipeline types, defaults to {"data-flow", "machine-learning-training", "sagemaker-training"}
     */
    @Parameter(defaultValue = "data-flow,machine-learning-training,sagemaker-training")
    private List<String> targetPipelineTypes;

    /**
     * Directory used for finding data record modules, for example: ${project.basedir}/{project-name}-shared/
     */
    @Parameter(defaultValue = "${project.parent.parent.basedir}/${project.parent.parent.artifactId}-shared")
    private String dataRecordsDirectory;


    /**
     * Configure the plugin to include supporting data record python modules with data pipelines. This property is
     * ignored if there are no semantic data metamodels. Defaults to empty list
     */
    @Parameter()
    private List<String> dataRecordModules;

    /**
     * Necessary for getting the correct version of the python pipeline artifact from the dist/ folder
     */
    @Parameter(required = true)
    private String habushuArtifactVersion;

    @Parameter()
    private String mavenDependencyPluginVersion;

    @Parameter()
    private String mavenResourcesPluginVersion;

    @Parameter(readonly = true, defaultValue = "${plugin}")
    private PluginDescriptor plugin;

    @Parameter(required = true, readonly = true, defaultValue = "${project}")
    private MavenProject mavenProject;

    @Parameter(required = true, readonly = true, defaultValue = "${session}")
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;

    /**
     * Sets attributes to be used throughout the plugin
     */
    public void setupPlugin() {
        //creates the environment to execute the plugins in
        this.environment = executionEnvironment(
                this.mavenProject,
                this.mavenSession,
                this.pluginManager
        );
    }

    /**
     * Creates a {@link ModelRepositoryConfiguration} used for reading in the MDA models.
     * @return {@link ModelRepositoryConfiguration}
     */
    public ModelRepositoryConfiguration setupConfig() {
        //create the MDA Model Repository Config
        ModelRepositoryConfiguration config = new ModelRepositoryConfiguration();

        //get the filepath for the location of where the mda models are located
        String modelsFilePath = getModelsFilePath();

        //update the config with the file path
        Map<String, ModelInstanceUrl> configModelsUrl = config.getMetamodelInstanceLocations();
        configModelsUrl.put(this.modelsArtifactId, new ModelInstanceUrl(this.modelsArtifactId, modelsFilePath));

        return config;
    }


    /**
     * Reads in the MDA models and calls the appropriate helper function based on the implementation type
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    @Override
    public void execute() throws MojoExecutionException {
        //set all the necessary attributes
        setupPlugin();

        //caller helper function for setting up the config
        ModelRepositoryConfiguration config = setupConfig();

        //Create a model repository from the config
        AIOpsModelInstanceRepostory repository = new AIOpsModelInstanceRepostory(config);
        ModelInstanceRepositoryManager.setRepository(repository);

        //Load and validate the models
        repository.load();
        repository.validate();

        logger.debug("Copying artifacts for pipelines of type: {}", this.targetPipelineTypes);
        logger.debug("Copying artifact of type: {}", this.targetArtifactTypes);
        dataRecordModules = dataRecordModules == null ? findDataRecordModules() : dataRecordModules;
        logger.debug("Copying data record modules: {}", dataRecordModules);

        //Retrieve and iterate through all the pipeline models
        Map<String, Pipeline> pipelines = repository.getPipelinesByArtifactId(this.modelsArtifactId);

        //use the factory to get the appropriate artifact copier for each pipeline
        ArtifactCopierFactory artifactCopierFactory = new ArtifactCopierFactory();

        for (Pipeline pipeline : pipelines.values()) {
            List<ArtifactCopier> artifactCopiers = artifactCopierFactory.makeCopiers(pipeline, dataRecordModules);

            for (ArtifactCopier copier: artifactCopiers) {
                if (copier.isTargeted(this)) {
                    copier.copyArtifact(this);
                } else {
                    logger.info("Skipping '{}' from '{}' of type '{}' since it is not targeted by the plugin configuration",
                            copier.getArtifactType(),
                            pipeline.getName(),
                            pipeline.getType().getName());
                }
            }
        }

        //Creates a blank requirements and wheels folders within the output directory. This is necessary for:
        // -spark worker dockerfile (spark-worker.docker.file.vm) to build for a project without pyspark pipelines
        // -airflow dockerfile (airflow.docker.file.vm) to build for a project without ML training steps
        // Otherwise, this directory is used to install requirements and monorepo dependencies first to leverage docker
        // build cache when requirements don't change.
        List<Path> directories = List.of(
                Paths.get(this.outputDirectory, "requirements"),
                Paths.get(this.outputDirectory, "wheels"));
        for (Path directory : directories) {
            if(!Files.exists(directory)) {
                try {
                    Files.createDirectories(directory);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Discovers python data record modules in the data records directory, so that their wheels can be copied to support
     * PySpark pipelines.  This isn't needed for Java pipelines, as the Java data records are included in the shaded jar
     * of the pipeline.
     * @return list of python data record modules in the data records directory
     */
    private List<String> findDataRecordModules() {
        if (StringUtils.isNotBlank(dataRecordsDirectory)) {
            Path dataRecordParentModule = Paths.get(dataRecordsDirectory);
            if(Files.exists(dataRecordParentModule) && Files.isDirectory(dataRecordParentModule)) {
                try(Stream<Path> list = Files.list(dataRecordParentModule)) {
                    return list.filter(Files::isDirectory)
                            .filter(dir -> dir.getFileName().toString().contains("data-records")) //name filter first as it's more efficient
                            .filter(this::isHabushuModule)
                            .map(Path::getFileName)
                            .map(Path::toString)
                            .collect(Collectors.toList());
                } catch (IOException e) {
                    logger.warn("Failed to list data record modules in " + dataRecordParentModule, e);
                }
            }
        }
        return Collections.emptyList();
    }

    private boolean isHabushuModule(Path moduleDir) {
        Path pom = moduleDir.resolve("pom.xml");
        if (Files.exists(pom) && Files.isRegularFile(pom)) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            try {
                Model model = reader.read(Files.newInputStream(pom));
                return "habushu".equals(model.getPackaging());
            } catch (IOException | XmlPullParserException e) {
                logger.warn("Failed to load packaging type of pom: " + pom, e);
            }
        }
        return false;
    }

    public boolean isTargeting(PipelineType pipelineType) {
        return targetPipelineTypes.stream()
                .map(PipelineType::fromString)
                .anyMatch(pipelineType::equals);
    }

    public boolean isTargeting(ArtifactType artifactType) {
        return targetArtifactTypes.contains(artifactType);
    }

    public boolean hasSemanticData() {
        return SemanticDataUtil.hasSemanticDataByArtifactId(modelsArtifactId);
    }

    /**
     * Copies the necessary artifacts for a machine learning pipeline with a training step to the desired output
     * directory by making use of the maven-resources-plugin.
     * @param pipelineName name of pipeline with the training step
     * @param pipelineStepName name of training step artifact to be copied
     */
    public void getMlTrainingStepArtifact(String pipelineName, String pipelineStepName) throws MojoExecutionException {
        //todo it would be better if we could just use the existing artifact copiers for tarball and reqs, but requires some refactoring
        //convert the pipeline and training step name to its artifact id
        String pipelineArtifactId = deriveArtifactIdFromCamelCase(pipelineName);
        String pipelineStepArtifactId = deriveArtifactIdFromCamelCase(pipelineStepName);

        Path distDirectory = Paths.get(this.pipelinesDirectory, pipelineArtifactId, pipelineStepArtifactId, "dist");
        Path tarballOutput = Paths.get(this.outputDirectory, pipelineArtifactId, pipelineStepArtifactId);
        Path requirementsOutput = Paths.get(this.outputDirectory, "requirements", pipelineArtifactId, pipelineStepArtifactId);

        //gathers the .tar.gz and puts it at outputDirectory/{pipeline name}/{pipeline step name}/
        copyResource(tarballOutput, distDirectory, getTarballPattern());
        //gathers the requirements.txt and puts it at outputDirectory/{pipeline name}/{pipeline step name}/
        copyResource(requirementsOutput, distDirectory, "requirements.txt");
    }

    /**
     * Copies the wheel file for a data record module to the desired output directory by making use of the
     * maven-resources-plugin.
     *
     * @param dataModule name of the data record module
     * @throws MojoExecutionException if the maven-resources-plugin fails to execute
     */
    public void getDataModuleArtifact(String dataModule) throws MojoExecutionException {
        //todo it would be better if we could just use the existing artifact copiers for wheels, but requires some refactoring
        Path distDirectory = Paths.get(this.dataRecordsDirectory, dataModule, "dist");
        Path wheelOutput = Paths.get(this.outputDirectory, "wheels");

        //gathers the .whl and puts it at outputDirectory/{data module name}/
        copyResource(wheelOutput, distDirectory, "*-" + this.habushuArtifactVersion + "*.whl");
    }

    /**
     * Copies the SparkApplications for a data-delivery pipeline to the desired output directory.
     * @param pipelineName Name of pipeline artifact to be copied.
     * @param archiveDir Directory containing the archive to be extracted
     * @param implType Implementation type of the pipeline being processed
     */
    public void retrieveSparkApplications(String pipelineName, Path archiveDir, PipelineImplementationEnum implType) throws MojoExecutionException {
        String extension = getArchiveExtension(implType);
        try {
            Optional<Path> archiveOpt = Files.find(archiveDir, 2, ((path, basicFileAttributes) -> path.toFile().getName().endsWith(extension))).findFirst();
            if (!archiveOpt.isPresent()) {
                throw new NoSuchFileException("No archive present in directory " + archiveDir);
            }
            Path archive = archiveOpt.get();
            FileSystem fileSystem = FileSystems.newFileSystem(archive, null);
            Path appsDirToExtract = fileSystem.getPath(getPathToValuesFileDir(implType, pipelineName));
            Files.walk(appsDirToExtract, 1).forEach((fileToExtract -> {
                try {
                    if (fileToExtract.getFileName().toString().endsWith(".yaml")) {
                        Path destination = Path.of(this.outputDirectory, fileToExtract.getFileName().toString());
                        Files.createDirectories(destination.getParent());
                        Files.copy(fileToExtract, Path.of(this.outputDirectory, fileToExtract.getFileName().toString()));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
            fileSystem.close();
            Files.delete(archive); // Because we always just grab the first jar, we need to delete it after we're done
        } catch(NoSuchFileException e) {
            logger.warn("No archive found in " + archiveDir.toString() + " with extension " + extension);
        } catch(IOException e) {
            throw new MojoExecutionException(e);
        }
    }

    /**
     * Retrieves the pyspark pipeline requirements.txt file
     *
     * @param pipelineName Artifact ID of the associated pyspark pipeline
     * @throws MojoExecutionException
     */
    public void retrieveRequirements(String pipelineName) throws MojoExecutionException {
        String pipelineArtifactId = deriveArtifactIdFromCamelCase(pipelineName);
        Path srcDirectory = getDistForPipelineArtifact(pipelineArtifactId);
        Path outDirectory = Paths.get(this.outputDirectory).resolve("requirements").resolve(pipelineArtifactId);

        copyResource(outDirectory, srcDirectory, "requirements.txt");
    }

    /**
     * Retrieves the tarball of the pyspark pipeline
     *
     * @param pipelineName Name of the pipeline being processed
     * @throws MojoExecutionException
     */
    public void retrieveTarball(String pipelineName) throws MojoExecutionException {
        String pipelineArtifactId = deriveArtifactIdFromCamelCase(pipelineName);
        Path srcDirectory = getDistForPipelineArtifact(pipelineArtifactId);
        Path outDirectory = Paths.get(outputDirectory).resolve(pipelineArtifactId);

        //gathers the .tar.gz and puts it at outputDirectory/{pipeline name}/
        copyResource(outDirectory, srcDirectory, getTarballPattern());
    }

    /**
     * Retrieves the individual driver script of the pyspark pipeline
     *
     * @param pipelineName Name of the pipeline being processed
     * @throws MojoExecutionException
     */
    public void retrieveDriver(String pipelineName) throws MojoExecutionException {
        String pipelineArtifactId = deriveArtifactIdFromCamelCase(pipelineName);
        String pipelinePythonName = deriveLowercaseSnakeCaseNameFromCamelCase(pipelineName);
        String driverName = pipelinePythonName + "_driver.py";
        Path fullOutputDir = Paths.get(outputDirectory).resolve(pipelineArtifactId);
        Path driverDirectory = Path.of(pipelinesDirectory, pipelineArtifactId, "src", pipelinePythonName);

        //gathers the driver.py and puts it at outputDirectory/{pipeline name}/
        copyResource(fullOutputDir, driverDirectory, driverName);
    }

    public void retrieveWheel(String pipelineName) throws MojoExecutionException {
        retrieveWheel(pipelineName, Paths.get(this.outputDirectory));
    }

    /**
     * Retrieves the pyspark pipeline wheel
     *
     * @param pipelineName Artifact ID of the associated pyspark pipeline
     * @param outputDir Base directory in which to place the copied artifact
     * @throws MojoExecutionException
     */
    public Path retrieveWheel(String pipelineName, Path outputDir) throws MojoExecutionException {
        String pipelineArtifactId = deriveArtifactIdFromCamelCase(pipelineName);
        Path distDirectory = getDistForPipelineArtifact(pipelineArtifactId);

        //gathers the .whl and puts them at outputDirectory/{pipeline name}/
        Path fullOutputDir = outputDir.resolve(pipelineArtifactId);
        copyResource(fullOutputDir, distDirectory, "*-" + this.habushuArtifactVersion + "*.whl");
        return outputDir;
    }

    public void retrieveSparkJar(String pipelineName) {
        retrieveSparkJar(pipelineName, Paths.get(this.outputDirectory));
    }

    /**
     * Copies the jar for a data-delivery-spark pipeline to the desired output directory by making use
     * of the maven-dependency-plugin.
     * @param pipelineName name of pipeline artifact to be copied
     */
    public Path retrieveSparkJar(String pipelineName, Path outputDir) {
        //convert the pipeline name to its artifact id
        String pipelineArtifactId = deriveArtifactIdFromCamelCase(pipelineName);
        outputDir = outputDir.toAbsolutePath();

        Plugin plugin = this.getPlugin("org.apache.maven.plugins", "maven-dependency-plugin", this.mavenDependencyPluginVersion);

        String goal = goal("copy");

        Xpp3Dom configuration = configuration(
                element("artifactItems",
                        element("artifactItem",
                                element(name("groupId"), this.groupId),
                                element(name("artifactId"), pipelineArtifactId),
                                element(name("version"), this.mavenProject.getVersion()),
                                element(name("type"), "jar"),
                                element(name("overWrite"), "true"),
                                element(name("destFileName"), pipelineArtifactId + ".jar"),
                                element(name("outputDirectory"), outputDir.toString())
                        )
                )
        );

        //Catch error where pipeline artifact is not found. This stops the build from failing when a new pipeline is
        //added but the manual action to add it to the {project}-pipelines/pom.xml module has not been completed yet.
        try {
            //maven-dependency-plugin automatically provides logging for the artifact its getting and output directory
            executeMojo(plugin, goal, configuration, this.environment);
        } catch (MojoExecutionException e) {
            if (e.getCause() != null && e.getCause().getCause() != null &&
                    e.getCause().getCause() instanceof ArtifactResolutionException) {
                String message = "Unable to locate the pipeline artifact: " + this.groupId + ":" + pipelineArtifactId +
                        ":jar:" + this.mavenProject.getVersion() + ". If you would like to include this artifact, " +
                        "be sure the module is being built in the "  + this.pipelinesDirectory + "/pom.xml " +
                        "to activate it in your build!";
                logger.warn(message);
            } else {
                throw new RuntimeException(e);
            }
        }
        return outputDir;
    }

    /**
     * Configures and executes the maven-resources-plugin to copy the provided resource from the source directory to the
     * output directory.
     *
     * @param outDirectory directory to copy the resource to
     * @param srcDirectory directory to copy the resource from
     * @param resource resource to be copied
     * @throws MojoExecutionException if the maven-resources-plugin fails to execute
     */
    private void copyResource(Path outDirectory, Path srcDirectory, String resource) throws MojoExecutionException {
        // Absolute paths shouldn't be necessary in typical usage, but they are necessary for testing and don't hurt
        srcDirectory = srcDirectory.toAbsolutePath();
        outDirectory = outDirectory.toAbsolutePath();
        Plugin plugin1 = this.getPlugin("org.apache.maven.plugins", "maven-resources-plugin", this.mavenResourcesPluginVersion);
        String goal = goal("copy-resources");
        Xpp3Dom artifactConfiguration = configuration(
                element(name("outputDirectory"), outDirectory.toString()),
                element(name("resources"), element(name("resource"),
                        element(name("directory"), srcDirectory.toString()),
                        element(name("filtering"), "false"),
                        element(name("includes"), element(name("include"), resource))
                ))
        );

        logger.info("Attempting to get artifact: " + srcDirectory.resolve(resource));
        executeMojo(plugin1, goal, artifactConfiguration, environment);
        logger.info("Copied to: " + outDirectory);
    }

    /**
     * Gets the plugin version from the project.properties located at mda-maven-plugin/src/main/resources/. This allows
     * plugin versions from the aiSSEMBLE build parent to be used in this plugin by default.
     * @param pluginPropertyName name of property in project.properties
     * @return {@link String} plugin version
     */
    private String getPluginVersion(String pluginPropertyName) {
        Properties props = new Properties();
        try {
            props.load(PipelineArtifactsMojo.class.getClassLoader().getResourceAsStream("project.properties"));
            String version =  props.getProperty(pluginPropertyName);

            if (version == null) {
                throw new RuntimeException("Missing property: '" + pluginPropertyName + "'. Ensure this property and" +
                        " value are set in mda-maven-plugin/src/main/resources/project.properties.");
            } else {
                return version;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the configured {@link Plugin} based on the provided parameters.
     * @param groupId
     * @param artifactId
     * @param version
     * @return
     */
    private Plugin getPlugin(String groupId, String artifactId, String version) {
        //sets the maven resources plugin version to version from build parent if none is provided
        if (version == null) {
            version = getPluginVersion(artifactId + "-version");
        }

        Plugin plugin = plugin(
                groupId(groupId),
                artifactId(artifactId),
                version(version)
        );

        return plugin;
    }

    /**
     * Finds the {@link #modelsArtifactId} artifact from the maven repo and provides the filepath to it. If an
     * alternated model source is provided, it returns that filepath instead.
     * @return {@link String} filepath
     */
    private String getModelsFilePath() {
        //get the models from the maven repository if no local source is provided
        if (this.modelsSource == null) {
            try {
                List<Artifact> artifacts = this.plugin.getArtifacts();

                Artifact modelArtifact = artifacts.stream()
                        .filter(artifact -> this.modelsArtifactId.equals(artifact.getArtifactId()))
                        .findFirst()
                        .orElse(null);

                return modelArtifact.getFile().toURI().toURL().toString();
            } catch (Exception e) {
                throw new RuntimeException("Unable to find an artifact with the ID: " + this.modelsArtifactId + ". Ensure " +
                        "the artifact is listed as a dependency for this plugin.", e);
            }
        }
        //get the models from a local source
        else {
            try {
                return this.modelsSource.toURI().toURL().toString();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Identifies the path to the directory within a pipeline's archive in which to search for values files to extract
     * @param implType Pipeline implementation type
     * @param pipelineName Name of the pipeline being processed
     * @return Path to the spark apps within the archive
     */
    private String getPathToValuesFileDir(PipelineImplementationEnum implType, String pipelineName) {
        switch(implType) {
            case DATA_DELIVERY_PYSPARK: return deriveLowercaseSnakeCaseNameFromCamelCase(pipelineName) + "/resources/apps/";
            case DATA_DELIVERY_SPARK: return "apps/";
            default: throw new IllegalArgumentException("Illegal pipeline type received for SparkApplication extraction: " + implType.name());
        }
    }

    /**
     * Identifies the file extension for the archive for a given pipeline
     *
     * @param implType Pipeline implementation type
     * @return target file extension
     */
    private String getArchiveExtension(PipelineImplementationEnum implType) {
        switch(implType) {
            case DATA_DELIVERY_PYSPARK: return ".whl";
            case DATA_DELIVERY_SPARK: return ".jar";
            default: throw new IllegalArgumentException("Cannot extract values files from pipeline of type " + implType);
        }
    }

    private String getTarballPattern() {
        return "*-" + habushuArtifactVersion + ".tar.gz";
    }

    private Path getDistForPipelineArtifact(String pipelineArtifactId) {
        return Paths.get(pipelinesDirectory, pipelineArtifactId, "dist");
    }

    /**** the following setters are primarily intended for testing purposes ****/

    public void setModelsSource(File modelsSource) {
        this.modelsSource = modelsSource;
    }

    public void setPipelinesDirectory(String pipelinesDirectory) {
        this.pipelinesDirectory = pipelinesDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setTargetPipelineTypes(List<String> targetPipelineTypes) {
        this.targetPipelineTypes = targetPipelineTypes;
    }

    public void setTargetArtifactTypes(List<String> targetArtifactTypes) {
        this.targetArtifactTypes = targetArtifactTypes.stream()
                .map(ArtifactType::valueOf)
                .collect(Collectors.toList());
    }

    public void setDataRecordsDirectory(String dataRecordsDirectory) {
        this.dataRecordsDirectory = dataRecordsDirectory;
    }

    public void setDataRecordModules(List<String> dataRecordModules) {
        this.dataRecordModules = dataRecordModules;
    }
}
