package com.boozallen.aiops.data.lineage;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.data.lineage.config.ConfigUtil;
import org.aeonbits.owner.KrauseningConfigFactory;

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
