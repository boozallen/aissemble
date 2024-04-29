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

import org.aeonbits.owner.KrauseningConfig;

/**
 * 
 * This is an extension of the KrauseningConfig class for the object-store-configuration.properties
 * file.
 *
 */
@KrauseningConfig.KrauseningSources("object-store-configuration.properties")
public interface ObjectStoreConfig extends KrauseningConfig {
    /**
     * S3 Access Key Value, required for access to s3 buckets.
     * @return String
     */
    @Key("s3.access.key")
    String getS3AccessKey();
    
    /**
     * S3 Secret Key Value, required for access to s3 buckets.
     * @return String
     */
    @Key("s3.secret.key")
    String getS3SecretKey();
    
    /**
     * S3 Session Token Value, generally used for temporary credentials.
     * @return String
     */
    @Key("s3.session.token")
    String getS3SessionToken();
    
    /**
     * S3 Region Value 
     * <br>
     * Example values: <br>
     * s3.us-east-1.amazonaws.com <br>
     * s3.us-west-2.amazonaws.com <br>
     * s3.af-south-1.amazonaws.com <br>
     * s3.eu-west-3.amazonaws.com <br>
     * 
     * @return String
     */
    @Key("s3.region")
    String getS3Region();
    
    /**
     * S3 bucket location where the write test will attempt to create an object.
     * <br>
     * Expected:<br>
     * s3a://{bucketName}/{newObjectToWrite}
     * @return String
     */
    @Key("s3.write.path")
    String getS3WritePath();
    
    /**
     * S3 bucket location the read test will attempt to access.
     * <br>
     * Expected:<br>
     * s3a://{bucketName}/{existingObjectToRead}
     * @return String
     */
    @Key("s3.read.path")
    String getS3ReadPath();
    
    /**
     * Google Cloud Service user name
     * @return String
     */
    @Key("gcs.user.name")
    String getGcsUserName();
    
    /**
     * Google Cloud Service password
     * @return String
     */
    @Key("gcs.password")
    String getGcsPassword();
    
    /**
     * Google Cloud Service location where the write test will attempt to create an object.
     * @return String
     */
    @Key("gcs.write.path")
    String getGcsWritePath();
    
    /**
     * Google Cloud Service location the read test will attempt to access.
     * @return String
     */
    @Key("gcs.read.path")
    String getGcsReadPath();
    
    /**
     * Azure object store user name
     * @return String
     */
    @Key("azure.user.name")
    String getAzureUserName();
    
    /**
     * Azure object store password
     * @return String
     */
    @Key("azure.password")
    String getAzurePassword();
    
    /**
     * Azure object store location where the write test will attempt to create an object.
     * @return String
     */
    @Key("azure.write.path")
    String getAzureWritePath();
    
    /**
     * Azure object store location the read test will attempt to access.
     * @return String
     */
    @Key("azure.read.path")
    String getAzureReadPath();
    
    /**
     * Local object store location where the write test will attempt to create an object.
     * @return String
     */
    @Key("local.write.path")
    String getLocalWritePath();
    
    /**
     * Local object store location the read test will attempt to access.
     * @return String
     */
    @Key("local.read.path")
    String getLocalReadPath();
}
