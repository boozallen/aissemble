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

public abstract class RunFacet extends Facet<OpenLineage.RunFacet> {
    public RunFacet() {
        super();
    }

    public RunFacet(String schemaUrl) {
        super(schemaUrl);
    }
    
    /**
     * Builds a RunFacet object from the OpenLineage Client library
     * @return A RunFacet
     */
    public OpenLineage.RunFacet getOpenLineageFacet() {
        DefaultRunFacet olFacet = new DefaultRunFacet(this.getSchemaUrl(), this.getProducer());
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
     * Convert the OpenLineage RunFacet to aissemble RunFacet class
     * @param openLineageFacet
     * @return facet
     */
    public static RunFacet fromOpenLineage(OpenLineage.RunFacet openLineageFacet) {
        return new RunFacet() {
            @Override
            public OpenLineage.RunFacet getOpenLineageFacet() {
                return openLineageFacet;
            }
        };
    }

    public class DefaultRunFacet extends OpenLineage.DefaultRunFacet {
        private final URI schemaUrl;
        
        public DefaultRunFacet(String schemaUrl, URI producer) {
            super(producer);
            this.schemaUrl = URI.create(schemaUrl);
        }
        
        @Override
        public URI get_schemaURL() {
            return schemaUrl;
        }      
    }
}
