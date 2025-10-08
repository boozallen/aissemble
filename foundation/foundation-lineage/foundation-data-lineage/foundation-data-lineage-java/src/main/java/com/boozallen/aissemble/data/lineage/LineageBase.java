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
