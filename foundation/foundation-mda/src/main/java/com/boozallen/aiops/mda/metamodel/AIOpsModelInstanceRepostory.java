package com.boozallen.aiops.mda.metamodel;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.boozallen.aiops.mda.generator.config.deployment.DeploymentConfigurationManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.technologybrewery.fermenter.mda.GenerateSourcesHelper.LoggerDelegate;
import org.technologybrewery.fermenter.mda.generator.GenerationException;
import org.technologybrewery.fermenter.mda.metamodel.AbstractModelInstanceRepository;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceUrl;
import org.technologybrewery.fermenter.mda.metamodel.ModelRepositoryConfiguration;
import org.technologybrewery.fermenter.mda.util.MessageTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aiops.mda.metamodel.element.Composite;
import com.boozallen.aiops.mda.metamodel.element.Dictionary;
import com.boozallen.aiops.mda.metamodel.element.DictionaryType;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.Record;
import com.boozallen.aiops.mda.metamodel.json.AiopsMdaJsonUtils;

/**
 * Loads the AIOps metamodel constructs that will later be used for code generation.
 */
public class AIOpsModelInstanceRepostory extends AbstractModelInstanceRepository {
    private static final String DEPLOYMENT_CONFIG_FILE_NAME = "deployment-config.json";
    private static final Logger logger = LoggerFactory.getLogger(AIOpsModelInstanceRepostory.class);

    private DictionaryModelInstanceManager dictionaryManager = DictionaryModelInstanceManager.getInstance();
    private CompositeModelInstanceManager compositeManager = CompositeModelInstanceManager.getInstance();
    private RecordModelInstanceManager recordManager = RecordModelInstanceManager.getInstance();
    private PipelineModelInstanceManager pipelineManager = PipelineModelInstanceManager.getInstance();
    private DeploymentConfigurationManager deploymentConfigurationManager;

    /**
     * {@inheritDoc}
     */
    public AIOpsModelInstanceRepostory(ModelRepositoryConfiguration config) {
        super(config);
        AiopsMdaJsonUtils.configureCustomObjectMappper();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load() {
        pipelineManager.reset();
        recordManager.reset();
        compositeManager.reset();
        dictionaryManager.reset();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            deploymentConfigurationManager = objectMapper.readValue(new File(DEPLOYMENT_CONFIG_FILE_NAME), DeploymentConfigurationManager.class);
        } catch (IOException e) {
            logger.warn(DEPLOYMENT_CONFIG_FILE_NAME + " was not found.  Proceeding with defaults--");
            deploymentConfigurationManager = new DeploymentConfigurationManager();
        }

        Collection<ModelInstanceUrl> modelInstanceUrls = config.getMetamodelInstanceLocations().values();
        for (ModelInstanceUrl modelInstanceUrl : modelInstanceUrls) {
            long start = System.currentTimeMillis();
            dictionaryManager.loadMetadata(modelInstanceUrl, config);
            compositeManager.loadMetadata(modelInstanceUrl, config);
            recordManager.loadMetadata(modelInstanceUrl, config);
            pipelineManager.loadMetadata(modelInstanceUrl, config);

            if (logger.isInfoEnabled()) {
                long stop = System.currentTimeMillis();
                logger.info("Metamodel instances for artifactId '{}' have been loaded - {}ms",
                        modelInstanceUrl.getArtifactId(), (stop - start));
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        for (Dictionary dictionary : dictionaryManager.getMetadataElementWithoutPackage().values()) {
            dictionary.validate();
        }

        for (Composite composite : compositeManager.getMetadataElementWithoutPackage().values()) {
            composite.validate();
        }

        for (Record recordInstance : recordManager.getMetadataElementWithoutPackage().values()) {
            recordInstance.validate();
        }

        for (Pipeline pipeline : pipelineManager.getMetadataElementWithoutPackage().values()) {
            pipeline.validate();
        }

        MessageTracker messageTracker = MessageTracker.getInstance();
        LoggerDelegate loggerDelegate = new LoggerDelegateImpl();
        messageTracker.emitMessages(loggerDelegate);

        if (messageTracker.hasErrors()) {
            throw new GenerationException(
                    "Encountered one or more errors!  Please check your Maven output for details.");
        }

    }

    /**
     * Gets an pipeline by name from the current package.
     * 
     * @param name
     *            name of the pipeline to look up
     * @return instance of the {@link Pipeline} or null if none is found with the request name
     */
    public Pipeline getPipeline(String name) {
        return pipelineManager.getMetadataElementByPackageAndName(config.getBasePackage(), name);

    }

    /**
     * Gets an pipeline by name from the current package.
     * 
     * @param name
     *            name of the pipeline to look up
     * @param packageName
     *            the package in which to look for the request element
     * @return instance of the {@link Pipeline} or null if none is found with the request name
     */
    public Pipeline getPipeline(String packageName, String name) {
        return pipelineManager.getMetadataElementByPackageAndName(packageName, name);

    }

    /**
     * Gets all pipelines from the specified package.
     * 
     * @param packageName
     *            the requested package
     * @return all pipelines within the request package, keyed by name
     */
    public Map<String, Pipeline> getPipelines(String packageName) {
        return pipelineManager.getMetadataElementByPackage(packageName);
    }

    /**
     * Gets all pipelines from the specified artifact id.
     * 
     * @param artifactId
     *            the requested artifact id
     * @return all pipelines within the request artifact id, keyed by name
     */
    public Map<String, Pipeline> getPipelinesByArtifactId(String artifactId) {
        return pipelineManager.getMetadataByArtifactIdMap(artifactId);
    }

    /**
     * Retrieves pipelines based on a generation context.
     * 
     * @param context
     *            type of generation target context being used
     * @return map of pipelines
     */
    public Map<String, Pipeline> getPipelinesByContext(String context) {
        return pipelineManager.getMetadataElementByContext(context);
    }

    /**
     * Gets an record by name from the current package.
     * 
     * @param name
     *            name of the record to look up
     * @return instance of the {@link Record} or null if none is found with the request name
     */
    public Record getRecord(String name) {
        return recordManager.getMetadataElementByPackageAndName(config.getBasePackage(), name);

    }

    /**
     * Gets an record by name from the current package.
     * 
     * @param name
     *            name of the record to look up
     * @param packageName
     *            the package in which to look for the request element
     * @return instance of the {@link Record} or null if none is found with the request name
     */
    public Record getRecord(String packageName, String name) {
        return recordManager.getMetadataElementByPackageAndName(packageName, name);

    }

    /**
     * Gets all records from the specified package.
     * 
     * @param packageName
     *            the requested package
     * @return all records within the request package, keyed by name
     */
    public Map<String, Record> getRecords(String packageName) {
        return recordManager.getMetadataElementByPackage(packageName);
    }

    /**
     * Gets all records from the specified artifact id.
     * 
     * @param artifactId
     *            the requested artifact id
     * @return all records within the request artifact id, keyed by name
     */
    public Map<String, Record> getRecordsByArtifactId(String artifactId) {
        return recordManager.getMetadataByArtifactIdMap(artifactId);
    }

    /**
     * Retrieves records based on a generation context.
     * 
     * @param context
     *            type of generation target context being used
     * @return map of records
     */
    public Map<String, Record> getRecordsByContext(String context) {
        return recordManager.getMetadataElementByContext(context);
    }

    /**
     * Gets an composite by name from the current package.
     * 
     * @param name
     *            name of the composite to look up
     * @return instance of the {@link Composite} or null if none is found with the request name
     */
    public Composite getComposite(String name) {
        return compositeManager.getMetadataElementByPackageAndName(config.getBasePackage(), name);

    }

    /**
     * Gets an composite by name from the current package.
     * 
     * @param name
     *            name of the composite to look up
     * @param packageName
     *            the package in which to look for the request element
     * @return instance of the {@link Composite} or null if none is found with the request name
     */
    public Composite getComposite(String packageName, String name) {
        return compositeManager.getMetadataElementByPackageAndName(packageName, name);

    }

    /**
     * Gets all composites from the specified package.
     * 
     * @param packageName
     *            the requested package
     * @return all composites within the request package, keyed by name
     */
    public Map<String, Composite> getComposites(String packageName) {
        return compositeManager.getMetadataElementByPackage(packageName);
    }

    /**
     * Gets all composites from the specified artifact id.
     * 
     * @param artifactId
     *            the requested artifact id
     * @return all composites within the request artifact id, keyed by name
     */
    public Map<String, Composite> getCompositesByArtifactId(String artifactId) {
        return compositeManager.getMetadataByArtifactIdMap(artifactId);
    }

    /**
     * Retrieves composites based on a generation context.
     * 
     * @param context
     *            type of generation target context being used
     * @return map of composites
     */
    public Map<String, Composite> getCompositesByContext(String context) {
        return compositeManager.getMetadataElementByContext(context);
    }

    /**
     * Gets a dictionary by name from the current package.
     * 
     * @param name
     *            name of the dictionary to look up
     * @return instance of the {@link Dictionary} or null if none is found with the request name
     */
    public Dictionary getDictionary(String name) {
        return dictionaryManager.getMetadataElementByPackageAndName(config.getBasePackage(), name);

    }

    /**
     * Gets all dictionaries from the specified artifact id.
     * 
     * @param artifactId
     *            the requested artifact id
     * @return all dictionaries within the request artifact id, keyed by name
     */
    public Map<String, Dictionary> getDictionariesByArtifactId(String artifactId) {
        return dictionaryManager.getMetadataByArtifactIdMap(artifactId);
    }

    /**
     * Retrieves dictionaries based on a generation context.
     * 
     * @param context
     *            type of generation target context being used
     * @return map of dictionaries
     */
    public Map<String, Dictionary> getDictionariesByContext(String context) {
        return dictionaryManager.getMetadataElementByContext(context);
    }

    /**
     * Gets a dictionary by name from the current package.
     * 
     * @param name
     *            name of the dictionary to look up
     * @param packageName
     *            the package in which to look for the request element
     * @return instance of the {@link Dictionary} or null if none is found with the request name
     */
    public Dictionary getDictionary(String packageName, String name) {
        return dictionaryManager.getMetadataElementByPackageAndName(packageName, name);

    }

    /**
     * Gets a dictionary type by name from the current package.
     * 
     * @param name
     *            name of the dictionary type to look up
     * @return instance of the {@link DictionaryType} or null if none is found with the request name
     */
    public DictionaryType getDictionaryType(String name) {
        return getDictionaryType(config.getBasePackage(), name);

    }

    /**
     * Gets a dictionary type by name from the current package.
     * 
     * @param packageName
     *            the package in which to look for the request element
     * @param name
     *            name of the dictionary type to look up
     * @return instance of the {@link DictionaryType} or null if none is found with the request name
     */
    public DictionaryType getDictionaryType(String packageName, String name) {
        String fullyQualifiedName = packageName + "." + name;
        Map<String, DictionaryType> dictionaryTypes = dictionaryManager.getDictionaryTypesByFullyQualifiedName();
        return dictionaryTypes.get(fullyQualifiedName);

    }

    /**
     * Gets the global deployment configuration manager
     *
     * @return Object representing the json configuration with global deployment settings
     */
    public DeploymentConfigurationManager getDeploymentConfigurationManager() { return deploymentConfigurationManager; }

}
