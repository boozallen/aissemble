package com.boozallen.aiops.core.metadata;

/*-
 * #%L
 * AIOps Core::Metadata::AIOps Metadata
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.List;
import java.util.Map;

public interface MetadataAPI {
    /**
     * Create metadata.
     * @param metadata the metadata to create.
     */
    void createMetadata(MetadataModel metadata);

    /**
     * Get metadata from search criteria.
     * @param searchParams the list of search parameters by key-value pair.
     * @return a list of metadata matching the search parameters.
     */
    List<MetadataModel> getMetadata(Map<String, Object> searchParams);
}
