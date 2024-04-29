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

public abstract class OutputDatasetFacet extends Facet<OpenLineage.OutputDatasetFacet>{
    public OutputDatasetFacet() {
        super();
    }

    public OutputDatasetFacet(String schemaUrl) {
        super(schemaUrl);
    }
    
    /**
     * Builds a OutputDatasetFacet object from the OpenLineage Client library
     * @return A OutputDatasetFacet
     */
    @Override
    public OpenLineage.OutputDatasetFacet getOpenLineageFacet() {
        DefaultOutputDatasetFacet olFacet = new DefaultOutputDatasetFacet(this.getSchemaUrl(), this.getProducer());
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
     * Convert the OpenLineage OuputDatasetFacet to aissemble OuputDatasetFacet class
     * @param openLineageFacet
     * @return facet
     */
    public static OutputDatasetFacet fromOpenLineage(OpenLineage.OutputDatasetFacet openLineageFacet) {
        return new OutputDatasetFacet() {
            @Override
            public OpenLineage.OutputDatasetFacet getOpenLineageFacet() {
                return openLineageFacet;
            }
        };
    }

    public class DefaultOutputDatasetFacet extends OpenLineage.DefaultOutputDatasetFacet {
        private final URI schemaUrl;

        public DefaultOutputDatasetFacet(String schemaUrl, URI producer) {
            super(producer);
            this.schemaUrl = URI.create(schemaUrl);
        }
        
        @Override
        public URI get_schemaURL() {
            return schemaUrl;
        }      
    }
}
