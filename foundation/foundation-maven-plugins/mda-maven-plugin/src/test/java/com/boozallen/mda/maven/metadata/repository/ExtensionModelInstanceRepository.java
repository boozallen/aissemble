package com.boozallen.mda.maven.metadata.repository;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Maven Plugins::MDA Maven Plugin
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.technologybrewery.fermenter.mda.metamodel.ModelRepositoryConfiguration;
import org.technologybrewery.fermenter.mda.util.JsonUtils;

import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import com.boozallen.aiops.mda.metamodel.element.PipelineType;
import com.boozallen.aiops.mda.metamodel.element.PipelineTypeElement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/*
 * Class extending the aissemble metamodel repository with our custom classes
 */
public class ExtensionModelInstanceRepository extends AissembleModelInstanceRepository {
    public static class PipelineTypeElementExtension extends PipelineTypeElement {
        public String simpleField;
    }

    public ExtensionModelInstanceRepository(ModelRepositoryConfiguration config) {
        super(config);
        this.configureCustomObjectMapper();
    }

    public void configureCustomObjectMapper() {
        SimpleModule module = new SimpleModule();

        // Add custom Pipeline extension
        module.addAbstractTypeMapping(PipelineType.class, PipelineTypeElementExtension.class);

        ObjectMapper localMapper = new ObjectMapper();
        localMapper.registerModule(module);
        JsonUtils.setObjectMapper(localMapper);
    }
}
