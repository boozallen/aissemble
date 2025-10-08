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
