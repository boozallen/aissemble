package com.boozallen.aiops.mda.generator.common;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.apache.maven.shared.utils.StringUtils;

import com.boozallen.aiops.mda.metamodel.element.Persist;

/**
 * Enum to represent a persist type.
 */
public enum PersistType {

    DELTA_LAKE("delta-lake"),
    HIVE("hive"),
    POSTGRES("postgres"),
    RDBMS("rdbms"),
    ELASTICSEARCH("elasticsearch"),
    NEO4J("neo4j")
    ;

    private String value;

    private PersistType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Checks if a persist element has a persist type equal to the given persist
     * type.
     * 
     * @param persist
     *            the persist element to check
     * @param persistType
     *            the persist type to compare
     * @return true if the persist element has a persist type equal to the given
     *         persist type
     */
    public static boolean hasPersistType(Persist persist, PersistType persistType) {
        boolean isEqual = false;

        if (persist != null && StringUtils.isNotBlank(persist.getType())) {
            String persistTypeToCheck = persist.getType();
            isEqual = persistTypeToCheck.equals(persistType.getValue());
        }

        return isEqual;
    }

}
