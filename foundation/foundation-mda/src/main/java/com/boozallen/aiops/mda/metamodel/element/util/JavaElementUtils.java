package com.boozallen.aiops.mda.metamodel.element.util;

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

import org.apache.commons.lang3.StringUtils;

/**
 * Common utilities for Java elements.
 */
public class JavaElementUtils {

    public static final String VALIDATION_EXCEPTION_IMPORT = "com.boozallen.aissemble.core.exception.ValidationException";
    public static final String ROW_FACTORY_IMPORT = "org.apache.spark.sql.RowFactory";
    public static final String ARRAY_LIST_IMPORT = "java.util.ArrayList";
    public static final String STRING_UTILS_IMPORT = "org.apache.commons.lang3.StringUtils";
    public static final String ARRAYS_IMPORT = "java.util.Arrays";
    public static final String ROUNDING_MODE_IMPORT = "java.math.RoundingMode";
    public static final String MAP_IMPORT = "java.util.Map";
    public static final String HASH_MAP_IMPORT = "java.util.HashMap";
    public static final String LIST_IMPORT = "java.util.List";
    public static final String SET_IMPORT = "java.util.Set";

    private JavaElementUtils() {
    }

    /**
     * Whether an import is needed for the given fully qualified type.
     * 
     * @param fullyQualifiedType
     *            the fully qualified typed to check
     * @return true if an import is needed
     */
    public static boolean isImportNeeded(String fullyQualifiedType) {
        return StringUtils.isNotBlank(fullyQualifiedType) && !fullyQualifiedType.startsWith("java.lang");
    }

}
