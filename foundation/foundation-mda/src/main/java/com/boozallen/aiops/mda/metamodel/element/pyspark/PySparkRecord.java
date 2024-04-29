package com.boozallen.aiops.mda.metamodel.element.pyspark;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.boozallen.aiops.mda.generator.common.FrameworkEnum;
import com.boozallen.aiops.mda.metamodel.element.Framework;
import com.boozallen.aiops.mda.metamodel.element.FrameworkElement;
import com.boozallen.aiops.mda.metamodel.element.python.PythonRecordField;
import com.boozallen.aiops.mda.metamodel.element.util.PythonElementUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aiops.mda.metamodel.element.Record;
import com.boozallen.aiops.mda.metamodel.element.RecordField;
import com.boozallen.aiops.mda.metamodel.element.python.PythonRecord;

/**
 * Decorates Record with PySpark-specific functionality.
 */
public class PySparkRecord extends PythonRecord {

    private static final Logger logger = LoggerFactory.getLogger(PySparkRecord.class);
    private static final String ROW_PACKAGE = "pyspark.sql.Row";

    private Set<String> imports = new TreeSet<>();

    /**
     * {@inheritDoc}
     */
    public PySparkRecord(Record recordToDecorate) {
        super(recordToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getBaseImports() {
        imports.addAll(super.getBaseImports());
        final String rowImport = PythonElementUtils.derivePythonImport(ROW_PACKAGE);
        imports.add(rowImport);
        return imports;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Framework> getFrameworks() {
        final List<Framework> frameworks = super.getFrameworks();
        final FrameworkElement pySparkFramework = new FrameworkElement();

        pySparkFramework.setName(FrameworkEnum.PYSPARK);
        frameworks.add(pySparkFramework);

        return frameworks;
    }

    /**
     * Convenience method for checking if a record contains the framework
     * @param framework the framework to check
     * @return true if the framework is supported
     */
    public boolean hasFramework(String framework) {
        return getFrameworks().stream()
                .anyMatch(fw -> fw.getName().equalsIgnoreCase(framework));
    }

}
