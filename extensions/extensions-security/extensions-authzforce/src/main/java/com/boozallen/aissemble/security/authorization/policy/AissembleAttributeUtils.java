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

import org.ow2.authzforce.xacml.identifiers.XacmlAttributeCategory;
import org.ow2.authzforce.xacml.identifiers.XacmlDatatypeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;

/**
 * Utility methods for fetching attributes.
 */
public final class AissembleAttributeUtils {
    private static final Logger logger = LoggerFactory.getLogger(AissembleAttributeUtils.class);

    private AissembleAttributeUtils() {
        // private constructor to prevent instantiation of all static class
    }

    /**
     * Translates a Aissemble attribute into the more complex, but standards-compliance XACML version.
     * 
     * @param attribute
     *            aissemble attribute
     * @return XACML version of the attribute
     */
    public static AttributeDesignatorType translateAttributeToXacmlFormat(AissembleAttribute attribute) {
        String category = attribute.getCategory();
        String id = attribute.getId();
        String type = attribute.getType();
        boolean mustBePresent = attribute.isRequired();

        String xacmlCategory = getXacmlCategory(category);
        String xacmlType = getXacmlType(type);

        return new AttributeDesignatorType(xacmlCategory, id, xacmlType, null, mustBePresent);
    }

    /**
     * Transforms the shortened version of categories used to simplify things Aissemble into their fulL XACML format.
     * 
     * @param category
     *            aissemble category
     * @return XACML category
     */
    public static String getXacmlCategory(String category) {
        String xacmlCategoy;
        switch (category) {
        case "resource":
            xacmlCategoy = XacmlAttributeCategory.XACML_3_0_RESOURCE.value();
            break;
        case "action":
            xacmlCategoy = XacmlAttributeCategory.XACML_3_0_ACTION.value();
            break;
        case "subject":
            xacmlCategoy = XacmlAttributeCategory.XACML_1_0_ACCESS_SUBJECT.value();
            break;
        default:
            xacmlCategoy = category;
            logger.warn("Unknown aissemble XACML category type '{}' - using value as is!", xacmlCategoy);
        }
        return xacmlCategoy;
    }

    /**
     * Transforms the shortened version of types used to simplify things aissemble into their fulL XACML format.
     * 
     * @param type
     *            aissemble type
     * @return XACML type
     */
    public static String getXacmlType(String type) {
        String xacmlType;
        switch (type) {
        case "string":
            xacmlType = XacmlDatatypeId.STRING.value();
            break;
        case "boolean":
            xacmlType = XacmlDatatypeId.BOOLEAN.value();
            break;
        case "anyUri":
        case "uri":
            xacmlType = XacmlDatatypeId.ANY_URI.value();
            break;
        case "date":
            xacmlType = XacmlDatatypeId.DATE.value();
            break;
        case "int":
        case "integer":
            xacmlType = XacmlDatatypeId.INTEGER.value();
            break;
        case "double":
            xacmlType = XacmlDatatypeId.DOUBLE.value();
            break;
        default:
            xacmlType = type;
            logger.warn("Unknown aissemble XAML attribute type '{}' - using value as is!", xacmlType);
        }

        return xacmlType;
    }

}
