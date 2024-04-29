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
