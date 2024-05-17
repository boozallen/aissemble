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
import io.openlineage.client.OpenLineage.OutputDatasetOutputFacets;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a set of input data that was modified, accessed, written, etc during a pipeline execution. More
 * granularity about this data can be captured using Facets contained in an OutputDataset
 */
public class OutputDataset extends Dataset {
    private Map<String, OutputDatasetFacet> outputFacets;

    public OutputDataset(String name, Map<String, DatasetFacet> facets) {
        super(name, facets);
    }

    public OutputDataset(String name, Map<String, DatasetFacet> facets, Map<String, OutputDatasetFacet> outputFacets) {
        super(name, facets);
        this.outputFacets = outputFacets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OpenLineage.OutputDataset getOpenLineageDataset()  {
        URI producer = URI.create(util.getProducer());
        OpenLineage openLineage = new OpenLineage(producer);

        DatasetFacets datasetFacets = openLineage.newDatasetFacetsBuilder().build();
        OutputDatasetOutputFacets outputDatasetFacets = openLineage.newOutputDatasetOutputFacetsBuilder().build();
        if(facets != null) {
            HashMap<String, OpenLineage.DatasetFacet> facetMap = new HashMap<>();
            for (Map.Entry<String, DatasetFacet> facetEntry: facets.entrySet()) {
                DatasetFacet facet = facetEntry.getValue();
                OpenLineage.DatasetFacet olFacet = facet.getOpenLineageFacet();
                facetMap.put(facetEntry.getKey(), olFacet);
            }
            datasetFacets.getAdditionalProperties().putAll(facetMap);
        }
        if(outputFacets != null) {
            HashMap<String, OpenLineage.OutputDatasetFacet> outputFacetMap = new HashMap<>();
            for (Map.Entry<String, OutputDatasetFacet> facetEntry: outputFacets.entrySet()) {
                OutputDatasetFacet facet = facetEntry.getValue();
                OpenLineage.OutputDatasetFacet olFacet = facet.getOpenLineageFacet();
                outputFacetMap.put(facetEntry.getKey(), olFacet);
            }
            outputDatasetFacets.getAdditionalProperties().putAll(outputFacetMap);
        }

        return openLineage.newOutputDataset(getNamespace(), getName(), datasetFacets, outputDatasetFacets);
    }

    /**
     *  get output facets
     * @return output facets
     */
    public Map<String, OutputDatasetFacet> getOutputFacets() {
        return outputFacets;
    }

    /**
     * get output facets
     * @param outputFacets Facets
     */
    public void setOutputFacets(Map<String, OutputDatasetFacet> outputFacets) {
        this.outputFacets = outputFacets;
    }
}
