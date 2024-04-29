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

public enum SparkStorageEnum {
    S3LOCAL("s3-local");

    private final String storageType;

    SparkStorageEnum(String storageType) {
        this.storageType = storageType;
    }

    public boolean equalsIgnoreCase(String compareType){
        return this.storageType.equalsIgnoreCase(compareType);
    }

    public String getStorageType() { return storageType; }

    public static boolean isValidStorageType(String storageType) {
        SparkStorageEnum[] storageTypes = values();
        for (SparkStorageEnum storage : storageTypes) {
            if (storage.equalsIgnoreCase(storageType)) {
                return true;
            }
        }
        return false;
    }
}
