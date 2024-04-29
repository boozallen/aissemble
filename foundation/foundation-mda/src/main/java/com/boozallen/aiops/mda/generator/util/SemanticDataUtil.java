package com.boozallen.aiops.mda.generator.util;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import com.boozallen.aiops.mda.metamodel.element.Dictionary;
import com.boozallen.aiops.mda.metamodel.element.Record;
import org.apache.commons.collections4.MapUtils;
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
     * @param metadataContext the current generation metadata context
     * @return whether semantic data is present
     */
    public static boolean hasSemanticDataByContext(String metadataContext) {
        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

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
        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

        Map<String, Dictionary> dictionaryMap = metamodelRepository.getDictionariesByArtifactId(artifactId);
        Map<String, Record> recordMap = metamodelRepository.getRecordsByArtifactId(artifactId);
        return !MapUtils.isEmpty(dictionaryMap) || !MapUtils.isEmpty(recordMap);
    }

    public static boolean arePythonDataRecordsNeeded(String metadataContext) {
        return hasSemanticDataByContext(metadataContext)
                && PipelineUtils.getDataFlowPipelines(metadataContext).hasPySparkPipelines();
    }

    public static boolean areJavaDataRecordsNeeded(String metadataContext) {
        return hasSemanticDataByContext(metadataContext)
                && PipelineUtils.getDataFlowPipelines(metadataContext).hasSparkPipelines();
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
