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

public enum ObjectStoreEnum {
    S3_ACCESS_KEY("s3.access.key"),
    S3_SECRET_KEY("s3.secret.key"),
    S3_SESSION_TOKEN("s3.session.token"),
    S3_REGION("s3.region"),
    S3_READ_PATH("s3.read.path"),
    S3_WRITE_PATH("s3.write.path"),

    GCS_USER_NAME("gcs.user.name"),
    GCS_PASSWORD("gcs.password"),
    GCS_READ_PATH("gcs.read.path"),
    GCS_WRITE_PATH("gcs.write.path"),

    AZURE_USER_NAME("azure.user.name"),
    AZURE_PASSWORD("azure.password"),
    AZURE_READ_PATH("azure.read.path"),
    AZURE_WRITE_PATH("azure.write.path"),

    LOCAL_READ_PATH("local.read.path"),
    LOCAL_WRITE_PATH("local.write.path");

    private String value;

    ObjectStoreEnum(final String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
