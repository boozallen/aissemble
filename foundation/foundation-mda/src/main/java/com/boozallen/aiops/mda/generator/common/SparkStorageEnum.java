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
