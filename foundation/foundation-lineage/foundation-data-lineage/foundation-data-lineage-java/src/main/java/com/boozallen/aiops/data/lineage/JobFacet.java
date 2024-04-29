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
import io.openlineage.client.OpenLineage;
import java.lang.reflect.Field;
import java.net.URI;

public abstract class JobFacet extends Facet<OpenLineage.JobFacet> {
    public JobFacet() {
        super();
    }

    public JobFacet(String schemaUrl) {
        super(schemaUrl);
    }
    
    /**
     * Builds a JobFacet object from the OpenLineage Client library
     * @return A JobFacet
     */
    public OpenLineage.JobFacet getOpenLineageFacet() {
        DefaultJobFacet olFacet = new DefaultJobFacet(this.getSchemaUrl(), this.getProducer());
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
     * Convert the OpenLineage JobFacet to aissemble JobFacet class
     * @param openLineageFacet
     * @return facet
     */
    public static JobFacet fromOpenLineage(OpenLineage.JobFacet openLineageFacet) {
        return new JobFacet() {
            @Override
            public OpenLineage.JobFacet getOpenLineageFacet() {
                return openLineageFacet;
            }
        };
    }

    public class DefaultJobFacet extends OpenLineage.DefaultJobFacet {
        private final URI schemaUrl;
        
        public DefaultJobFacet(String schemaUrl, URI producer) {
            super(producer);
            this.schemaUrl = URI.create(schemaUrl);
        }
        
        @Override
        public URI get_schemaURL() {
            return schemaUrl;
        }      
    }
}
