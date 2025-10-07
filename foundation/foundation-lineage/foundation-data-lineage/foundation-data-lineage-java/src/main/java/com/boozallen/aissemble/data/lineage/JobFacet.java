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
            super(producer, null);
            this.schemaUrl = URI.create(schemaUrl);
        }
        
        @Override
        public URI get_schemaURL() {
            return schemaUrl;
        }      
    }
}
