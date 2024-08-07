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


/**
 * A Job represents a pipeline step execution in a Data Lineage event, and contains information describing
 * that execution.
 */
public class Job extends LineageBase<JobFacet> {

    private String name;
    private String defaultNamespace;
    private static ConfigUtil util = ConfigUtil.getInstance();

    public Job(String name) {
        this(name, null, null);
    }

    public Job(String name, Map<String, JobFacet> facets) {
        this(name, facets, null);
    }

    public Job(String name, String defaultNamespace) {
        this(name, null, defaultNamespace);
    }

    public Job(String name, Map<String, JobFacet> facets, String defaultNamespace) {
        super(facets);
        this.name = name;
        this.defaultNamespace = defaultNamespace;
    }

    /**
     * Returns a Job object from the OpenLineage Client library
     * @return A Job
     */
    protected OpenLineage.Job getOpenLineageJob() {
        String producerConfig = util.getProducer(this.name);

        URI producer = URI.create(producerConfig);
        OpenLineage openLineage = new OpenLineage(producer);

        OpenLineage.JobFacets jobFacets = openLineage.newJobFacetsBuilder().build();
        if(facets != null) {
            HashMap<String, OpenLineage.JobFacet> facetMap = new HashMap<>();
            for (Map.Entry<String, JobFacet> facetEntry: facets.entrySet()) {
                JobFacet facet = facetEntry.getValue();
                OpenLineage.JobFacet olFacet = facet.getOpenLineageFacet();
                facetMap.put(facetEntry.getKey(), olFacet);
            }
            jobFacets.getAdditionalProperties().putAll(facetMap);
        }

        return openLineage.newJob(util.getJobNamespace(this.name, this.defaultNamespace), this.name, jobFacets);
    }

    /**
     * Accessor for the name field
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name field value
     * @param name The name of this Job
     */
    public void setName(String name) {
        this.name = name;
    }
}
