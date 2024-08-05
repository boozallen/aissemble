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

import java.util.List;

public interface QueryService {
    List<? extends AbstractDataAccessRecord> executeQuery(String table,
                                                          Class<? extends AbstractDataAccessRecord> recordType);

    List<? extends AbstractDataAccessRecord> executeQuery(String table, DataAccessWhereClause[] where,
                                                                 Class<? extends AbstractDataAccessRecord> recordType);

    List<? extends AbstractDataAccessRecord> executeQuery(String table, Integer limit,
                                                                 Class<? extends AbstractDataAccessRecord> recordType);

    List<? extends AbstractDataAccessRecord> executeQuery(String table, DataAccessWhereClause[] where, Integer limit,
                                                                 Class<? extends AbstractDataAccessRecord> recordType);
}
