package com.boozallen.aiops.mda.generator.util;

/*-
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

import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import com.boozallen.aiops.mda.metamodel.element.Dictionary;
import com.boozallen.aiops.mda.metamodel.element.Record;
import org.apache.commons.collections4.MapUtils;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import java.util.Map;

/**
 * Utility methods for dealing with semantic data, i.e. {@link com.boozallen.aiops.mda.metamodel.element.Dictionary} and
 * {@link com.boozallen.aiops.mda.metamodel.element.Record} metamodels.
 */
public class SemanticDataUtil {

    /**
     * Returns true if the specified {@code context} has any semantic data, including Dictionary and Record metamodels.
     *
     * @param generationContext the current generation context
     * @param metadataContext the current generation metadata context
     * @return whether semantic data is present
     */
    public static boolean hasSemanticDataByContext(GenerationContext generationContext, String metadataContext) {
        AissembleModelInstanceRepository metamodelRepository = (AissembleModelInstanceRepository) generationContext.getModelInstanceRepository();

        Map<String, Dictionary> dictionaryMap = metamodelRepository.getDictionariesByContext(metadataContext);
        Map<String, Record> recordMap = metamodelRepository.getRecordsByContext(metadataContext);
        return !dictionaryMap.isEmpty() || !recordMap.isEmpty();
    }

    /**
     * Returns true if the specified {@code context} has any semantic data, including Dictionary and Record metamodels.
     *
     * @param artifactId the current generation metadata context
     * @return whether semantic data is present
     */
    public static boolean hasSemanticDataByArtifactId(String artifactId) {
        // Must use ModelInstanceRepositoryManager as this method does not have access to the GenerationContext
        AissembleModelInstanceRepository metamodelRepository = ModelInstanceRepositoryManager
            .getMetamodelRepository(AissembleModelInstanceRepository.class);

        Map<String, Dictionary> dictionaryMap = metamodelRepository.getDictionariesByArtifactId(artifactId);
        Map<String, Record> recordMap = metamodelRepository.getRecordsByArtifactId(artifactId);
        return !MapUtils.isEmpty(dictionaryMap) || !MapUtils.isEmpty(recordMap);
    }

    public static boolean arePythonDataRecordsNeeded(GenerationContext generationContext, String metadataContext) {
        return hasSemanticDataByContext(generationContext, metadataContext)
                && PipelineUtils.getDataFlowPipelines(generationContext, metadataContext).hasPySparkPipelines();
    }

    public static boolean areJavaDataRecordsNeeded(GenerationContext generationContext, String metadataContext) {
        return hasSemanticDataByContext(generationContext, metadataContext)
                && PipelineUtils.getDataFlowPipelines(generationContext, metadataContext).hasSparkPipelines();
    }

    public enum DataRecordModule {
        COMBINED("data-records"),
        CORE("data-records-core"),
        SPARK("data-records-spark");

        private final String baseName;

        DataRecordModule(String baseName) {
            this.baseName = baseName;
        }

        public String getBaseName() {
            return baseName;
        }
    }
}
