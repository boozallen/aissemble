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

import com.boozallen.aiops.mda.generator.util.PipelineUtils;

/**
 * Common utilities for Python elements.
 */
public class PythonElementUtils {

    private PythonElementUtils() {
    }

    /**
     * Derives the Python import string for the given fully qualified type.
     * 
     * @param fullyQualifiedType
     *            the fully qualified type
     * @return the python import string
     */
    public static String derivePythonImport(String fullyQualifiedType) {
        // builds the python import like so:
        // 'some.package.Type' becomes 'from some.package import Type'
        String pythonImport = null;

        // we are assuming that if there is no '.' in the fully qualified
        // implementation, then it's a built-in python type and does not require
        // an import; otherwise we will parse out the python package from the
        // beginning to the last '.'
        int index = fullyQualifiedType.lastIndexOf(".");
        if (index > 0) {
            String pythonPackage = fullyQualifiedType.substring(0, index);
            String typeName = fullyQualifiedType.substring(index + 1);
            pythonImport = "from " + pythonPackage + " import " + typeName;
        }

        return pythonImport;
    }

    /**
     * Returns the pipeline name formatted into lowercase with underscores
     * (Python naming convention).
     *
     * @return the pipeline name formatted into lowercase with underscores
     */
    public static String getSnakeCaseValue(String camelCasedString) {
        return PipelineUtils.deriveLowercaseSnakeCaseNameFromCamelCase(camelCasedString);
    }

}
