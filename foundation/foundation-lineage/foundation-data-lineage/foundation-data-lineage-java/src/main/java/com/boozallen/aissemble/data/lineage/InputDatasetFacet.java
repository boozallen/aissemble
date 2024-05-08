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

public abstract class InputDatasetFacet extends Facet<OpenLineage.InputDatasetFacet> {
    public InputDatasetFacet() {
        super();
    }

    public InputDatasetFacet(String schemaUrl) {
        super(schemaUrl);
    }
    
    /**
     * Builds a InputDatasetFacet object from the OpenLineage Client library
     * @return A InputDatasetFacet
     */
    @Override
    public OpenLineage.InputDatasetFacet getOpenLineageFacet() {
        DefaultInputDatasetFacet olFacet = new DefaultInputDatasetFacet(this.getSchemaUrl(), this.getProducer());
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
     * Convert the OpenLineage InputDatasetFacet to aissemble InputDatasetFacet class
     * @param openLineageFacet
     * @return facet
     */
    public static InputDatasetFacet fromOpenLineage(OpenLineage.InputDatasetFacet openLineageFacet) {
        return new InputDatasetFacet() {
            @Override
            public OpenLineage.InputDatasetFacet getOpenLineageFacet() {
                return openLineageFacet;
            }
        };
    }

    public class DefaultInputDatasetFacet extends OpenLineage.DefaultInputDatasetFacet {
        private final URI schemaUrl;

        public DefaultInputDatasetFacet(String schemaUrl, URI producer) {
            super(producer);
            this.schemaUrl = URI.create(schemaUrl);
        }
        
        @Override
        public URI get_schemaURL() {
            return schemaUrl;
        }      
    }
}
