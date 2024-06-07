package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import com.boozallen.aiops.mda.util.TestMetamodelUtil;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.model.Scm;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.technologybrewery.fermenter.mda.GenerateSourcesHelper;
import org.technologybrewery.fermenter.mda.element.ExpandedProfile;
import org.technologybrewery.fermenter.mda.element.Target;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceUrl;
import org.technologybrewery.fermenter.mda.metamodel.ModelRepositoryConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.technologybrewery.fermenter.mda.reporting.StatisticsService;

public abstract class AbstractModelInstanceSteps {

    protected static final String BOOZ_ALLEN_PACKAGE = "com.boozallen.aiops.record";
    protected static final File GENERATED_METADATA_DIRECTORY = new File("target/temp-metadata");
    public static final String TEST_VERSION = "1.0.0-SNAPSHOT";

    private static final String AIOPS_MDA = "aiops-mda";
    protected static final String DICTIONARY_TYPE_TEST_STRING = "testString";

    protected File dictionariesDirectory = new File(GENERATED_METADATA_DIRECTORY, "dictionaries");
    protected File compositesDirectory = new File(GENERATED_METADATA_DIRECTORY, "composites");
    protected File recordsDirectory = new File(GENERATED_METADATA_DIRECTORY, "records");
    protected File pipelinesDirectory = new File(GENERATED_METADATA_DIRECTORY, "pipelines");

    protected ObjectMapper objectMapper = new ObjectMapper();
    protected AIOpsModelInstanceRepostory metadataRepo;
    protected Path projectDir;
    protected String projectName;
    protected PipelineElement pipeline;
    protected String scenario;
    protected Map<String, Pipeline> pipelines = new HashMap<>();
    protected String scmUrl;

    private static final Map<String, Integer> uniquenessCounter = new HashMap<>();

    protected void readMetadata() {
        readMetadata(AIOPS_MDA);
    }

    protected void readMetadata(String artifactId) {
        // reduce debugging output by ensuring expected directories exist:
        dictionariesDirectory.mkdirs();
        compositesDirectory.mkdirs();
        recordsDirectory.mkdirs();
        pipelinesDirectory.mkdirs();

        ModelRepositoryConfiguration config = new ModelRepositoryConfiguration();
        config.setArtifactId(artifactId);
        config.setBasePackage(BOOZ_ALLEN_PACKAGE);
        Map<String, ModelInstanceUrl> metadataUrlMap = config.getMetamodelInstanceLocations();
        metadataUrlMap.put(artifactId,
                new ModelInstanceUrl(artifactId, recordsDirectory.getParentFile().toURI().toString()));

        List<String> targetedInstances = config.getTargetModelInstances();
        targetedInstances.add(artifactId);

        metadataRepo = new AIOpsModelInstanceRepostory(config);
        ModelInstanceRepositoryManager.setRepository(metadataRepo);
        metadataRepo.load();
        metadataRepo.validate();

    }

    protected File getDictionaryFileByName(String name) {
        return createFileAndDirectories(dictionariesDirectory, name);
    }

    protected File getCompositeFileByName(String name) {
        return createFileAndDirectories(compositesDirectory, name);
    }

    protected File getRecordFileByName(String name) {
        return createFileAndDirectories(recordsDirectory, name);
    }

    public File getPipelineFileByName(String name) {
        return createFileAndDirectories(pipelinesDirectory, name);
    }

    private File createFileAndDirectories(File parent, String name) {
        File targetFile = new File(parent, name + ".json");
        targetFile.getParentFile().mkdirs();
        return targetFile;
    }

    protected File saveDictionaryToFile(DictionaryElement newDictionary) throws Exception {
        File dictionaryFile = getDictionaryFileByName(newDictionary.getName());
        objectMapper.writeValue(dictionaryFile, newDictionary);
        assertTrue("Target not written to file!", dictionaryFile.exists());

        return dictionaryFile;
    }

    protected File saveRecordToFile(RecordElement newRecord) {
        File recordFile = getRecordFileByName(newRecord.getName());
        try {
            objectMapper.writeValue(recordFile, newRecord);
        } catch (IOException e) {
            throw new RuntimeException("Problem saving record file!", e);
        }
        assertTrue("Target not written to file!", recordFile.exists());
        return recordFile;
    }

    protected void createSampleDictionaryType() throws Exception {
        DictionaryElement dictionary = new DictionaryElement();
        dictionary.setName("TestDictionary");
        dictionary.setPackage(BOOZ_ALLEN_PACKAGE);
        DictionaryTypeElement defaultType = new DictionaryTypeElement();
        defaultType.setName(DICTIONARY_TYPE_TEST_STRING);
        defaultType.setSimpleType("string");
        dictionary.addDictionaryType(defaultType);

        saveDictionaryToFile(dictionary);
    }

    protected void createSampleDictionary(List<DictionaryTypeElement> dictionaryTypes) throws Exception {
        DictionaryElement dictionary = new DictionaryElement();
        dictionary.setName("TestDictionaryWithTypeList");
        dictionary.setPackage(BOOZ_ALLEN_PACKAGE);
        for (DictionaryType type : dictionaryTypes) {
            dictionary.addDictionaryType(type);
        }

        saveDictionaryToFile(dictionary);
    }

    protected DictionaryTypeElement createDictionaryType(String name, String type) {
        DictionaryTypeElement protectionPolicyDictionaryTestType = new DictionaryTypeElement();
        protectionPolicyDictionaryTestType.setName(name);
        protectionPolicyDictionaryTestType.setSimpleType(type);

        return protectionPolicyDictionaryTestType;
    }

    protected void saveCompositeToFile(CompositeElement newComposite) {
        File compositeFile = getCompositeFileByName(newComposite.getName());
        try {
            objectMapper.writeValue(compositeFile, newComposite);
        } catch (IOException e) {
            throw new RuntimeException("Problem saving composite file!", e);
        }
        assertTrue("Target not written to file!", compositeFile.exists());
    }

    public File savePipelineToFile(PipelineElement pipeline) throws IOException {
        File pipelineFile = getPipelineFileByName(pipeline.getName());
        objectMapper.writeValue(pipelineFile, pipeline);
        assertTrue("Target not written to file!", pipelineFile.exists());

        return pipelineFile;
    }

    protected Map<String, ExpandedProfile> loadProfiles() throws IOException {
        try (FileInputStream profileIn = new FileInputStream("src/main/resources/profiles.json");
             FileInputStream targetIn = new FileInputStream("src/main/resources/targets.json")) {
            Map<String, ExpandedProfile> profiles = GenerateSourcesHelper.loadProfiles(profileIn, new HashMap<>());
            Map<String, Target> targets = GenerateSourcesHelper.loadTargets(targetIn, new HashMap<>());
            for (ExpandedProfile p : profiles.values()) {
                p.dereference(profiles, targets);
            }
            return profiles;
        }
    }

    protected GenerationContext createGenerationContext(Target target) {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty("resource.loader", "class");
        engine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        engine.init();
        GenerationContext context = new GenerationContext(target);
        context.setBasePackage(BOOZ_ALLEN_PACKAGE);
        context.setProjectDirectory(projectDir.toFile());
        context.setGeneratedSourceDirectory(projectDir.resolve("generated").toFile());
        context.setMainSourceDirectory(projectDir.resolve("main").toFile());
        context.setTestSourceDirectory(projectDir.resolve("test").toFile());
        context.setGeneratedTestSourceDirectory(projectDir.resolve("generated-test").toFile());
        context.setEngine(engine);
        context.setGroupId(BOOZ_ALLEN_PACKAGE);
        context.setArtifactId(projectName);
        context.setVersion(TEST_VERSION);
        context.setDescriptiveName(projectName);
        context.setExecutionRootDirectory(projectDir.toFile());
        context.setRootArtifactId(projectName);
        context.setStatisticsService(new StatisticsService(new NullSession()));
        context.setScmUrl(scmUrl);
        if(pipeline != null) {
            context.setPropertyVariables(Map.of("targetPipeline", pipeline.getName()));
        }
        return context;
    }

    protected static class NullSession extends MavenSession {
        public NullSession() {
            super(null, null, new DefaultMavenExecutionRequest(), null);
        }
    }

    protected static class Slf4jDelegate implements GenerateSourcesHelper.LoggerDelegate {
        private final Logger logger;

        public Slf4jDelegate(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void log(LogLevel level, String message) {
            Level slf4jLevel = Level.valueOf(level.name());
            this.logger.atLevel(slf4jLevel).log(message);
        }
    }

    protected static Path writePom(Model model, Path mavenDir) throws IOException {
        MavenXpp3Writer writer = new MavenXpp3Writer();
        Files.createDirectories(mavenDir);
        Path pom = mavenDir.resolve("pom.xml");
        OutputStream out = Files.newOutputStream(pom);
        writer.write(out, model);
        return pom;
    }

    protected void createProject(String projectName, String moduleName) throws IOException {
        createProject(projectName, moduleName, null);
    }

    protected void createProject(String projectName, String moduleName, String url) throws IOException {
        this.projectName = projectName;
        if(scenario != null) {
            this.projectDir = Paths.get("target", unique(scenario), this.projectName);
        } else {
            this.projectDir = Paths.get("target", this.projectName);
        }
        Model model = new Model();
        model.setArtifactId(projectName);
        model.setPackaging("pom");
        model.setGroupId(BOOZ_ALLEN_PACKAGE);
        model.setVersion(TEST_VERSION);
        model.setModules(List.of(projectName + "-" + moduleName));
        if(url != null) {
            Scm scm = new Scm();
            scm.setUrl(url);
            model.setScm(scm);
            this.scmUrl = url;
        }
        writePom(model, projectDir);
    }

    protected void createPipeline(String name, String typeName, String implName) throws IOException {
        createPipeline(name, typeName, implName, p -> {});
    }

    protected void createPipeline(String name, String typeName, String implName, Consumer<PipelineElement> customization) throws IOException {
        this.pipeline = TestMetamodelUtil.createPipelineWithType(name, BOOZ_ALLEN_PACKAGE, typeName, implName);
        customization.accept(pipeline);
        savePipelineToFile(pipeline);
        pipelines.put(name, pipeline);
    }

    protected static String unique(String name) {
        String uniqueName = name;
        int i = uniquenessCounter.getOrDefault(name, 1);
        if (i > 1) {
            uniqueName += i;
        }
        uniquenessCounter.put(name, i + 1);
        return uniqueName;
    }
}
