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

import com.boozallen.aissemble.data.lineage.config.ConfigUtil;

import java.net.URI;

/**
 * A Facet provides a method of customizing what information is captured in Data Lineage events by providing a hook
 * with which developers can add their own data structures to the overall schema, without modifying the core schema
 * elements that ensure it is compliant with a data lineage standard.
 */
public abstract class Facet<T> {
    private URI producer;
    private String schemaUrl;
    private static ConfigUtil util = ConfigUtil.getInstance();

    public Facet() {
        this.producer = URI.create(util.getProducer());
        this.schemaUrl = util.getDataLineageSchemaUrl();
    }

    public Facet(String schemaUrl) {
        this.producer = URI.create(util.getProducer());
        this.schemaUrl = schemaUrl;
    }

    public abstract T getOpenLineageFacet();
    
    /**
     * Accessor for the producer field
     * @return The producer for this Facet
     */
    public URI getProducer() {
        return producer;
    }

    /**
     * Sets the producer field value
     * @param producer The producer for this Facet
     */
    public void setProducer(URI producer) {
        this.producer = producer;
    }

    /**
     * Accessor for the schema URL field
     * @return The schema URL for this Facet
     */
    public String getSchemaUrl() {
        return schemaUrl;
    }

    /**
     * Sets the schema URL field value
     * @param schemaUrl The schema URL for this Facet
     */
    public void setSchemaUrl(String schemaUrl) {
        this.schemaUrl = schemaUrl;
    }
}
