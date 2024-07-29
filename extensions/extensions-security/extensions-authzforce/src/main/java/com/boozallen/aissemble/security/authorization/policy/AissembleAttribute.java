package com.boozallen.aissemble.security.authorization.policy;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Authzforce::Extensions::Security::Authzforce
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
