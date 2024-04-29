package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.technologybrewery.fermenter.mda.metamodel.element.NamespacedMetamodel;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Defines the contract for a record with one or more fields. A record field can refer to a dictionary type or a
 * composite type.
 */
public interface Record extends NamespacedMetamodel {
    /**
     * Gets the title of this record or name if not set.
     *
     * @return record description
     */
    String getTitle();

    /**
     * Returns the description of this record.
     * 
     * @return record description
     */
    String getDescription();

    /**
     * Returns the data access settings for this record.
     * 
     * @return data access
     */
    DataAccess getDataAccess();

    /**
     * Returns the fields contained in this field container.
     * 
     * @return fields
     */
    List<RecordField> getFields();

    /**
     * Returns the list of supported Frameworks (e.g. PySpark).
     * @return list of supported Frameworks.
     */
    List<Framework> getFrameworks();

    /**
     * Loops over all records until a record is found that tests true with predicate.
     * Otherwise, returns false.
     *
     * @param records
     * @param predicate
     * @return
     */
    static boolean aRecordExistsWhere(
            Collection<Record> records, Function<Record, Boolean> predicate) {
        boolean result = false;

        for (Record currentRecord : records) {
            result = predicate.apply(currentRecord);

            if (result) {
                break;
            }
        }

        return result;
    }

}
