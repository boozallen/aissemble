package com.boozallen.aissemble.data.access;

/*-
 * #%L
 * aiSSEMBLE Foundation::aiSSEMBLE Data Access
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Base data-access record class for accessing a record from the database.
 */
public abstract class AbstractDataAccessRecord {

    /**
     * Convenience method to map a ResultSet to a record.
     * 
     * @param resultSet
     *            the resultSet to map from
     * @throws SQLException
     */
    public abstract void mapResultSet(ResultSet resultSet) throws SQLException;

}
