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

public class DataAccessWhereClause {
    private String fieldName;
    private String value;
    private DataAccessWhereOperator operator;

    public DataAccessWhereClause(String fieldName, String value, DataAccessWhereOperator operator) {
        this.fieldName = fieldName;
        this.value = value;
        this.operator = operator;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getValue() {
        return value;
    }

    public DataAccessWhereOperator getOperator() {
        return operator;
    }

}
