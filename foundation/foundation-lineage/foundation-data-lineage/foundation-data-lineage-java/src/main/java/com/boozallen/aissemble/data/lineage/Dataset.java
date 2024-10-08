package com.boozallen.aissemble.data.lineage;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.data.lineage.config.ConfigUtil;
import io.openlineage.client.OpenLineage;

import java.util.Map;

/**
 * Represents a set of data that was modified, accessed, written, etc during a pipeline execution. More granularity
 * about this data can be captured using Facets contained in a Dataset
 */
public abstract class Dataset extends LineageBase<DatasetFacet> {

    private String name;
    private String namespace;
    protected static ConfigUtil util = ConfigUtil.getInstance();

    public Dataset(String name, Map<String, DatasetFacet> facets) {
        super(facets);
        this.name = name;
        this.namespace = util.getDatasetNamespace(this.name);
    }

    /**
     * Returns a Dataset object from the OpenLineage Client library
     * @return An OpenLineage Client Dataset of the given type
     */
    public abstract OpenLineage.Dataset getOpenLineageDataset();

    /**
     * Accessor for the name field
     * @return The name for this Dataset
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name field value
     * @param name The name for this Dataset
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Accessor for the namespace field
     * @return The namespace for this Dataset
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the namespace field value
     * @param namespace The namespace for this Dataset
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
