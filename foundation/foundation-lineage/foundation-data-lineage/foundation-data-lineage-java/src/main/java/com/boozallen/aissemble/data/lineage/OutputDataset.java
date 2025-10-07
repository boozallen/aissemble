package com.boozallen.aissemble.data.lineage;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Java
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
