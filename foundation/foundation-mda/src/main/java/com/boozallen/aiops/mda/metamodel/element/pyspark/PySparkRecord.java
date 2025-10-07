package com.boozallen.aiops.mda.metamodel.element.pyspark;

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

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.boozallen.aiops.mda.generator.common.FrameworkEnum;
import com.boozallen.aiops.mda.metamodel.element.Framework;
import com.boozallen.aiops.mda.metamodel.element.FrameworkElement;
import com.boozallen.aiops.mda.metamodel.element.util.PythonElementUtils;

import com.boozallen.aiops.mda.metamodel.element.Record;
import com.boozallen.aiops.mda.metamodel.element.python.PythonRecord;

/**
 * Decorates Record with PySpark-specific functionality.
 */
public class PySparkRecord extends PythonRecord {

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
