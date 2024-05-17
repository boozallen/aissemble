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

import java.util.Map;

/**
 * The base class for all Data Lineage objects, this allows for consistency in how child classes access their various
 * types of facets.
 */
public class LineageBase<T> {

    protected Map<String, T> facets;

    public  LineageBase(Map<String, T> facets) {
        this.facets = facets;
    }

    /**
     * Accessor for the list of facets
     * @return The list of facets
     */
    public Map<String, T> getFacets() {
        return facets;
    }

    /**
     * Sets the list of facets
     * @param facets The list of Facets to be associated with this Lineage object
     */
    public void setFacets(Map<String, T> facets) {
        this.facets = facets;
    }
}
