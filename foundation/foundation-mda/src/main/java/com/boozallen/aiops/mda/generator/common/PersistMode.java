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

/**
 * Enum to represent a persist mode.
 */
public enum PersistMode {

    APPEND("append"),
    ERROR("error"),
    IGNORE("ignore"),
    OVERWRITE("overwrite")
    ;

    private String modeType;

    private PersistMode(String modeType) {
        this.modeType = modeType;
    }

    public String getModeType() {
        return modeType;
    }

    /**
     * Checks if the given value is a valid persist mode.
     * 
     * @param value
     *            value to check
     * @return true if the value is a valid persist mode
     */
    public static boolean isValid(String value) {
        boolean valid = false;

        for (PersistMode persistMode : PersistMode.values()) {
            if (persistMode.getModeType().equals(value)) {
                valid = true;
                break;
            }
        }

        return valid;
    }

}
