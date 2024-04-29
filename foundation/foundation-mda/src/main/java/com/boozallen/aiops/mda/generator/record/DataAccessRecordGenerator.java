package com.boozallen.aiops.mda.generator.record;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.metamodel.element.Record;

/**
 * Generates a java class for a record in the data-access module if the record
 * has data access enabled.
 */
public class DataAccessRecordGenerator extends JavaRecordGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                | Template                                     | Generated File                         |
     * |-----------------------|----------------------------------------------|----------------------------------------|
     * | dataAccessRecordBase  | data-access/data.access.record.base.java.vm  | ${basePackage}/${recordName}Base.java  |
     * | dataAccessRecordImpl  | data-access/data.access.record.impl.java.vm  | ${basePackage}/${recordName}.java      |
     */

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldGenerateFile(Record currentRecord) {
        return currentRecord.getDataAccess() == null || currentRecord.getDataAccess().isEnabled();
    }
}
