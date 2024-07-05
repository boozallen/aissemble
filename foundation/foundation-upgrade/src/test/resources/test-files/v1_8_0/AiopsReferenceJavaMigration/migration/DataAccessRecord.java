package com.example.record;

/*-
 * #%L
 * dataaccess1::Pipelines::Data Access
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.data.access.AbstractDataAccessRecord;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.eclipse.microprofile.graphql.Ignore;
import org.eclipse.microprofile.graphql.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation of the Data Access record for DataAccessRecord.
 *
 * GENERATED CODE - DO NOT MODIFY (add your customizations in DataAccessRecord).
 *
 * Generated from: templates/data-access/data.access.record.base.java.vm
 */
public abstract class DataAccessRecordBase extends AbstractDataAccessRecord {

    private static final Logger logger = LoggerFactory.getLogger(DataAccessRecordBase.class);

    protected static final String EMPLOYMENT_STATUS_FIELD = "employmentStatus";

    @Name(EMPLOYMENT_STATUS_FIELD)
    private Boolean employmentStatus;

    public void setEmploymentStatus(Boolean employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public Boolean getEmploymentStatus() {
        return employmentStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapResultSet(ResultSet resultSet) throws SQLException {
        if (hasField(resultSet, EMPLOYMENT_STATUS_FIELD)) {
            Boolean employmentStatusValue = resultSet.getBoolean(EMPLOYMENT_STATUS_FIELD);
            setEmploymentStatus(employmentStatusValue);
        }
    }

    /**
     * Checks if a ResultSet contains a field.
     *
     * @param resultSet
     *            the result set to check
     * @param field
     *            the field to check
     * @return true if the ResultSet contains the field
     */
    @Ignore
    protected boolean hasField(ResultSet resultSet, String field) {
        boolean hasField = false;
        try {
            resultSet.getObject(field);
            hasField = true;
        } catch (SQLException e) {
            logger.debug("Result set does not have field {}", field);
        }

        return hasField;
    }

    /**
     * Represents the fields of a DataAccessRecord record.
     */
    public enum Field {
        EMPLOYMENT_STATUS(EMPLOYMENT_STATUS_FIELD, Boolean.class),
        ;

        private String name;
        private Class<?> type;

        private Field(String name, Class<?> type) {
            this.name = name;
            this.type = type;
        }

        /**
         * Returns the name of the field.
         *
         * @return name
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the type of the field.
         *
         * @return type
         */
        public Class<?> getType() {
            return type;
        }

    }

}
