package com.boozallen.aiops.mda.generator.common;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */
public enum PipelineExecuterHelperEnum {
	AIRFLOW("airflow");

    private final String executionHelperType;

    PipelineExecuterHelperEnum(String executionHelperType) {
        this.executionHelperType = executionHelperType;
    }

    public boolean equalsIgnoreCase(String compareType){
        return this.executionHelperType.equalsIgnoreCase(compareType);
    }

    public String getExecutionHelperType() { return executionHelperType; }

    public static boolean isValidHelper(String helperType) {
        PipelineExecuterHelperEnum[] executionHelperTypes = values();
        for (PipelineExecuterHelperEnum helper : executionHelperTypes) {
            if (helper.equalsIgnoreCase(helperType)) {
                return true;
            }
        }
        return false;
    }
}
