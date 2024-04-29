package com.boozallen.aiops.mda.metamodel.element.util;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
