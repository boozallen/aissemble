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

import io.openlineage.client.OpenLineage;
import io.openlineage.client.OpenLineage.DatasetFacets;
import io.openlineage.client.OpenLineage.InputDatasetInputFacets;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a set of input data that was modified, accessed, written, etc during a pipeline execution. More
 * granularity about this data can be captured using Facets contained in an InputDataset
 */
public class InputDataset extends Dataset {
    private Map<String, InputDatasetFacet> inputFacets;

    public InputDataset(String name, Map<String, DatasetFacet> facets) {
        super(name, facets);
    }

    public InputDataset(String name, Map<String, DatasetFacet> facets, Map<String, InputDatasetFacet> inputFacets){
        super(name, facets);
        this.inputFacets = inputFacets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OpenLineage.InputDataset getOpenLineageDataset()  {
        URI producer = URI.create(util.getProducer());
        OpenLineage openLineage = new OpenLineage(producer);

        DatasetFacets datasetFacets = openLineage.newDatasetFacetsBuilder().build();
        if(facets != null) {
            HashMap<String, OpenLineage.DatasetFacet> facetMap = new HashMap<>();
            for (Map.Entry<String, DatasetFacet> facetEntry: facets.entrySet()) {
                DatasetFacet facet = facetEntry.getValue();
                OpenLineage.DatasetFacet olFacet = facet.getOpenLineageFacet();
                facetMap.put(facetEntry.getKey(), olFacet);
            }
            datasetFacets.getAdditionalProperties().putAll(facetMap);
        }
        InputDatasetInputFacets inputDatasetFacets = openLineage.newInputDatasetInputFacetsBuilder().build();
        if(inputFacets != null){
            HashMap<String, OpenLineage.InputDatasetFacet> inputFacetMap = new HashMap<>();
            for (Map.Entry<String, InputDatasetFacet> facetEntry: inputFacets.entrySet()) {
                InputDatasetFacet facet = facetEntry.getValue();
                OpenLineage.InputDatasetFacet olFacet = facet.getOpenLineageFacet();
                inputFacetMap.put(facetEntry.getKey(), olFacet);
            }
            inputDatasetFacets.getAdditionalProperties().putAll(inputFacetMap);
        }
        return openLineage.newInputDataset(getNamespace(), getName(), datasetFacets, inputDatasetFacets);
    }

    /**
     * get input facets
     * @return input facets
     */
    public Map<String, InputDatasetFacet> getInputFacets() {
        return inputFacets;
    }

    /**
     * set input facets
     * @param inputFacets
     */
    public void setInputFacets(Map<String, InputDatasetFacet> inputFacets) {
        this.inputFacets = inputFacets;
    }
}

