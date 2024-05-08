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
import java.lang.reflect.Field;
import java.net.URI;

public abstract class DatasetFacet extends Facet<OpenLineage.DatasetFacet> {
    public DatasetFacet() {
        super();
    }

    public DatasetFacet(String schemaUrl) {
        super(schemaUrl);
    }
    
    /**
     * Builds a DatasetFacet object from the OpenLineage Client library
     * @return A DatasetFacet
     */
    public OpenLineage.DatasetFacet getOpenLineageFacet() {
        DefaultDatasetFacet olFacet = new DefaultDatasetFacet(this.getSchemaUrl(), this.getProducer());
        Class<?> facetClass = this.getClass();
        try {
            for(Field field : facetClass.getDeclaredFields()) {
                field.setAccessible(true);
                olFacet.getAdditionalProperties().put(field.getName(), field.get(this));
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return olFacet;
    }

    /**
     * Convert the OpenLineage DatasetFacet to aissemble DatasetFacet class
     * @param openLineageFacet
     * @return facet
     */
    public static DatasetFacet fromOpenLineage(OpenLineage.DatasetFacet openLineageFacet) {
        return new DatasetFacet() {
            @Override
            public OpenLineage.DatasetFacet getOpenLineageFacet() {
                return openLineageFacet;
            }
        };
    }

    public class DefaultDatasetFacet extends OpenLineage.DefaultDatasetFacet {
        private final URI schemaUrl;

        public DefaultDatasetFacet(String schemaUrl, URI producer) {
            super(producer);
            this.schemaUrl = URI.create(schemaUrl);
        }
        
        @Override
        public URI get_schemaURL() {
            return schemaUrl;
        }      
    }
}
