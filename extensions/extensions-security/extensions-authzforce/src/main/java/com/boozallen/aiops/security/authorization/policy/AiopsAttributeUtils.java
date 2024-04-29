package com.boozallen.aiops.security.authorization.policy;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Authzforce::Extensions::Security::Authzforce
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.security.authorization.policy.AiopsAttribute;
import org.ow2.authzforce.xacml.identifiers.XacmlAttributeCategory;
import org.ow2.authzforce.xacml.identifiers.XacmlDatatypeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;

/**
 * Utility methods for fetching attributes.
 */
public final class AiopsAttributeUtils {
    private static final Logger logger = LoggerFactory.getLogger(AiopsAttributeProvider.class);

    private AiopsAttributeUtils() {
        // private constructor to prevent instantiation of all static class
    }

    /**
     * Translates a Aiops attribute into the more complex, but standards-compliance XACML version.
     * 
     * @param attribute
     *            aiops attribute
     * @return XACML version of the attribute
     */
    public static AttributeDesignatorType translateAttributeToXacmlFormat(AiopsAttribute attribute) {
        String category = attribute.getCategory();
        String id = attribute.getId();
        String type = attribute.getType();
        boolean mustBePresent = attribute.isRequired();

        String xacmlCategory = getXacmlCategory(category);
        String xacmlType = getXacmlType(type);

        return new AttributeDesignatorType(xacmlCategory, id, xacmlType, null, mustBePresent);
    }

    /**
     * Transforms the shortened version of categories used to simplify things Aiops into their fulL XACML format.
     * 
     * @param category
     *            aiops category
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
            logger.warn("Unknown Aiops XACML category type '{}' - using value as is!", xacmlCategoy);
        }
        return xacmlCategoy;
    }

    /**
     * Transforms the shortened version of types used to simplify things aiops into their fulL XACML format.
     * 
     * @param type
     *            aiops type
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
            logger.warn("Unknown Aiops XAML attribute type '{}' - using value as is!", xacmlType);
        }

        return xacmlType;
    }

}
