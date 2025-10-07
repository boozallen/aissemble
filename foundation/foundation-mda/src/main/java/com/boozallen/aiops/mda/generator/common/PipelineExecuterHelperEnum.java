package com.boozallen.aiops.mda.generator.common;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
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
