package com.boozallen.aiops.mda.generator.common;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
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
