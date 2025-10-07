package com.boozallen.aissemble.security.authorization.policy;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Authzforce::Extensions::Security::Authzforce
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

import org.apache.commons.lang3.StringUtils;

/**
 * Represents the structure of an attribute for policy decision.
 */
public class AissembleAttribute {

    private String id;
    
    private String category;
    
    private String type;
    
    private boolean required;
    
    private String attributePointClass;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return StringUtils.trim(category);
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        if (StringUtils.isBlank(type)) {
            type = "string";
        }
        return StringUtils.trim(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getAttributePointClass() {
        return StringUtils.trim(attributePointClass);
    }

    public void setAttributeSourceClass(String attributePointClass) {
        this.attributePointClass = attributePointClass;
    }
    
}
