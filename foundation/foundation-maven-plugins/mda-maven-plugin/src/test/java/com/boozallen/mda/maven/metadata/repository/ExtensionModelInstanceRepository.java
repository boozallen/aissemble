package com.boozallen.mda.maven.metadata.repository;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Maven Plugins::MDA Maven Plugin
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
