package com.boozallen.aiops.data.delivery.spark.objectstore;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Data Delivery::Spark
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

/**
 * This class gives a third state in addition to true and false. This gives the
 * ability to return NOT_CONFIGURED to show that no value was set
 *
 */
public enum TriStateEnum {
    TRUE("Success"), 
    FALSE("Failure"), 
    NOT_CONFIGURED("Not Configured");

    private String value;

    TriStateEnum(final String string) {
        this.value = string;
    }

    /**
     * This will return the string value for the Enum
     * @return "Success", "Failure", or "Not Configured"
     */
    public String getStringValue() {
        return this.value;
    }

    /**
     * returns true only when enum is FALSE
     */
    public boolean isFailure() {
        boolean isFailure;
        if (this.getStringValue().compareTo(FALSE.getStringValue())==0) {
            isFailure = true;
        }
        else{
            isFailure = false;
        }
        return isFailure;
    }
}
