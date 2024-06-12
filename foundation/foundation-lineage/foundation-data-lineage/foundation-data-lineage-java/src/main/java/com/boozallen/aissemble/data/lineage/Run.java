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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A Run represents a pipeline execution in a Data Lineage event, and contains information describing
 * that execution.
 */
public class Run extends LineageBase<RunFacet> {

    private UUID runId;
    private static ConfigUtil util = ConfigUtil.getInstance();

    public Run(UUID runId) {
        this(runId, null);
    }

    public Run(UUID runId, Map<String, RunFacet> facets) {
        super(facets);
        this.runId = runId;
    }

    /**
     * Returns a Run object from the OpenLineage Client library
     * @return A Run
     */
    public OpenLineage.Run getOpenLineageRun() {
        URI producer = URI.create(util.getProducer());
        OpenLineage openLineage = new OpenLineage(producer);

        OpenLineage.RunFacets runFacets = openLineage.newRunFacetsBuilder().build();
        if(facets != null) {
            HashMap<String, OpenLineage.RunFacet> facetMap = new HashMap<>();
            for (Map.Entry<String, RunFacet> facetEntry: facets.entrySet()) {
                RunFacet facet = facetEntry.getValue();
                OpenLineage.RunFacet olFacet = facet.getOpenLineageFacet();
                facetMap.put(facetEntry.getKey(), olFacet);
            }
            runFacets.getAdditionalProperties().putAll(facetMap);
        }

        return openLineage.newRun(this.runId, runFacets);
    }

    /**
     * Accessor for the run ID field
     * @return The run ID
     */
    public UUID getRunId() {
        return runId;
    }

    /**
     * Sets the run ID field value
     * @param runId The new Run ID for this Run
     */
    public void setRunId(UUID runId) {
        this.runId = runId;
    }
}
