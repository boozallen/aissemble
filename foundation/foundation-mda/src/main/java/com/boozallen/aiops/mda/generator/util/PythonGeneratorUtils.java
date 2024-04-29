package com.boozallen.aiops.mda.generator.util;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

/**
 * Encapsulates Python specific MDA generator utility functions that may be used
 * across {@link org.technologybrewery.fermenter.mda.generator.Generator} hierarchies.
 */
public final class PythonGeneratorUtils {
    private PythonGeneratorUtils() {

    }

    /**
     * Populates the given {@link VelocityContext} (creating a new one if the given parameter is {@code null})
     * with Python specific attributes that are commonly used in Python-specific templates.
     *
     * @param velocityContext   Velocity context to populate with Python-specific template variables.
     * @param generationContext captures key generation-related metadata
     * @return given {@link VelocityContext} that has been appropriately updated.
     */
    public static VelocityContext populateCommonPythonContext(VelocityContext velocityContext, GenerationContext generationContext) {
        if (velocityContext == null) {
            velocityContext = new VelocityContext();
        }
        velocityContext.put(VelocityProperty.PYTHON_PACKAGE_NAME, generationContext.getArtifactId());
        velocityContext.put(VelocityProperty.PYTHON_PACKAGE_FOLDER_NAME, generationContext.getBasePackage());
        return velocityContext;
    }

    /**
     * Normalizes the given value (i.e. Python variable, package folder, etc.) to align
     * with PEP-8 naming conventions by replacing dashes with underscores.
     *
     * @return
     */
    public static String normalizeToPythonCase(String value) {
        return PipelineUtils.deriveLowerSnakeCaseNameFromHyphenatedString(value);
    }

    /**
     * Returns the Python-equivalent version of the provided Maven artifact version that aligns with
     * Habushu/aiSSEMBLE conventions of using Python developmental versions for Maven SNAPSHOT versions.
     * The returned Python version may be used in Poetry pyproject.toml configurations, pip requirements.txt
     * files, and other Python dependency configurations that rely on aiSSEMBLE Python components.
     * For example, if the given Maven dependency version is {@code 1.2.3-SNAPSHOT}, {@code ^1.2.3.dev}
     * will be returned as the corresponding Python version.
     *
     * @param mavenVersion Maven version to convert into the equivalent Python dependency version.
     * @return appropriately formatted Python dependency version that may be used in Poetry pyproject.toml,
     * pip requirements.txt, and other Python tools.
     */
    public static String getPythonDependencyVersion(String mavenVersion) {
        String pythonVersion = mavenVersion;
        if (StringUtils.isNotEmpty(mavenVersion)) {
            if (mavenVersion.endsWith("-SNAPSHOT")) {
                pythonVersion = StringUtils.substringBeforeLast(mavenVersion, "-SNAPSHOT") + ".*";
            }
        }
        return pythonVersion;
    }
}
