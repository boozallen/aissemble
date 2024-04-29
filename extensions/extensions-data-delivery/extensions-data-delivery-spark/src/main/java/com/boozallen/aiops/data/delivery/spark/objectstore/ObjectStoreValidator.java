package com.boozallen.aiops.data.delivery.spark.objectstore;

import java.util.ArrayList;
import java.util.List;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Data Delivery::Spark
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.shaded.org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.boozallen.aissemble.alerting.core.Alert;
import com.boozallen.aissemble.alerting.core.AlertProducer;

/**
 * This class will values configured with ObjectStoreConfig and attempt to use them
 * connect to the corresponding object store.
 * <br><br>
 * Currently we offer full support for s3 with partial support for Google cloud
 * service, Azure and local object stores.
 *
 */
@ApplicationScoped
public class ObjectStoreValidator {

    private static final Logger logger = LoggerFactory.getLogger(ObjectStoreValidator.class);

    private ObjectStoreConfig config = KrauseningConfigFactory.create(ObjectStoreConfig.class);

    @Inject
    protected AlertProducer alertProducer;

    /**
     * Stores the log message from a successful read and write test, used for testing.
     */
    public static final StringBuilder result = new StringBuilder();

    /**
     * This runs through the various objectStore endpoints (S3,Google Cloud Service,
     * Azure, and local) to determine if they can be read from and written to using
     * the credentials provided in krausening/base/object-store-configuration.properties.
     *
     */
    public void verifyObjectStoreConnections(SparkSession sparkSession) {
        if (alertProducer == null) {
            throw new RuntimeException("CDI Injection for AlertProducer failed! Please ensure ObjectStoreValidator is created using injection and not the \"new\" operator");
        }

        verifyS3Connection(sparkSession);
        verifyGCSConnection(sparkSession);
        verifyAzureConnection(sparkSession);
        verifyLocalConnection(sparkSession);
    }

    /**
     * Set the hadoopConfiguration credentials for the sparkSession and test the
     * read and write capabilities at the configured endpoint.
     */
    protected void verifyS3Connection(SparkSession sparkSession) {
        Configuration hadoopConfig = sparkSession.sparkContext().hadoopConfiguration();
        hadoopConfig.set("fs.s3a.access.key", config.getS3AccessKey());
        hadoopConfig.set("fs.s3a.secret.key", config.getS3SecretKey());
        hadoopConfig.set("fs.s3a.session.token", config.getS3SessionToken());
        hadoopConfig.set("fs.s3a.endpoint", config.getS3Region());
        TriStateEnum readStatus = testObjectStoreRead(config.getS3ReadPath(), sparkSession);
        TriStateEnum writeStatus = testObjectStoreWrite(config.getS3WritePath(), sparkSession);
        logObjectStoreConnection("S3", readStatus, writeStatus);
    }

    /**
     * Method stub to test the read and write capabilities of the configured
     * endpoint.
     */
    protected void verifyGCSConnection(SparkSession sparkSession) {
        TriStateEnum readStatus = testObjectStoreRead(config.getGcsReadPath(), sparkSession);
        TriStateEnum writeStatus = testObjectStoreWrite(config.getGcsWritePath(), sparkSession);
        logObjectStoreConnection("GCS", readStatus, writeStatus);
    }

    /**
     * Method stub to test the read and write capabilities of the configured
     * endpoint.
     */
    protected void verifyAzureConnection(SparkSession sparkSession) {
        TriStateEnum readStatus = testObjectStoreRead(config.getAzureReadPath(), sparkSession);
        TriStateEnum writeStatus = testObjectStoreWrite(config.getAzureWritePath(), sparkSession);
        logObjectStoreConnection("Azure", readStatus, writeStatus);
    }

    /**
     * Method stub to test the read and write capabilities of the configured
     * endpoint.
     */
    protected void verifyLocalConnection(SparkSession sparkSession) {
        TriStateEnum readStatus = testObjectStoreRead(config.getLocalReadPath(), sparkSession);
        TriStateEnum writeStatus = testObjectStoreWrite(config.getLocalWritePath(), sparkSession);
        logObjectStoreConnection("Local", readStatus, writeStatus);
    }

    /**
     * Using the readStatus and writeStatus, send a failure or success alert and log
     * the message locally.
     * <br><br>
     * Failure: if either write or read were configured but failed <br>
     * Success: if write and read were successful or not configured
     */
    private void logObjectStoreConnection(String objectStoreName, TriStateEnum readStatus, TriStateEnum writeStatus) {
        String msg = objectStoreName + " Connection Status: Read: " + readStatus.getStringValue() + ", Write: "
                + writeStatus.getStringValue();
        if ((readStatus.isFailure()) || (writeStatus.isFailure())) {
            alertProducer.sendAlert(Alert.Status.FAILURE, msg);
            logger.error(msg);
        } else {
            if (readStatus.equals(TriStateEnum.NOT_CONFIGURED)) {
                logger.warn("!!! CONNECTION TEST WAS SKIPPED FOR {} PLEASE SET THE FOLLOWING PROPERTIES IN ObjectStoreConfig \n {}" , objectStoreName, getPropertyNamesForObjectStore(objectStoreName));
                msg = "!!! CONNECTION TEST WAS SKIPPED FOR "+ objectStoreName +" PLEASE SET THE PROPERTIES IN ObjectStoreConfig";
            }
            else {
                logger.info(msg);
            }
            alertProducer.sendAlert(Alert.Status.SUCCESS, msg);
            result.append(msg + "\n");
        }
    }

    /**
     * Gathers the specific property names relevant to each object store
     * @param objectStoreName
     * @return Property names for objectStoreName or an emptry String[] if the objectStoreName doesn't have any properties.
     */
    private String[] getPropertyNamesForObjectStore(String objectStoreName) {
        
        if(objectStoreName.equals("S3")) {
            return new String[]{"s3.access.key", "s3.secret.key", "s3.session.token","s3.region","s3.write.path","s3.read.path"};
        }
        else if(objectStoreName.equals("GCS")) {
            return new String[]{"gcs.user.name","gcs.password","gcs.write.path","gcs.read.path"};
        }
        else if(objectStoreName.equals("Azure")) {
            return new String[]{"azure.user.name","azure.password","azure.write.path","azure.read.path"};
        }
        else if(objectStoreName.equals("Local")) {
            return new String[]{"local.write.path","local.read.path"};
        }
        else {
            return new String[]{};
        }
        
    }

    /**
     * attempt to read data at readPath using the configured credentials.
     * 
     * @return TriStateEnum (NOT_CONFIGURED, TRUE, FALSE)
     */
    protected TriStateEnum testObjectStoreRead(String readPath, SparkSession sparkSession) {
        TriStateEnum readSucceeded = TriStateEnum.FALSE;
        if (StringUtils.isBlank(readPath)) {
            readSucceeded = TriStateEnum.NOT_CONFIGURED;
        }
        else {
            logger.info("Starting Spark read verification for {}", readPath);
            Dataset<Row> testData = sparkSession.read().option("multiline", true).json(readPath);
            if (testData.count() > 0) {
                readSucceeded = TriStateEnum.TRUE;
            }
        }
        return readSucceeded;
    }

    /**
     * Attempt to write data at readPath using the configured credentials, verify
     * the write succeeded and then remove the data that was written.
     * 
     * @return TriStateEnum (NOT_CONFIGURED, TRUE, FALSE)
     */
    protected TriStateEnum testObjectStoreWrite(String writePath, SparkSession sparkSession) {
        TriStateEnum writeResult;
        if (StringUtils.isBlank(writePath)) {
            writeResult = TriStateEnum.NOT_CONFIGURED;
        }
        else {
            Dataset<Row> df = createTestData(sparkSession);
            df.write().mode(SaveMode.Overwrite).json(writePath);
    
            writeResult = testObjectStoreRead(writePath, sparkSession);
            cleanupS3Write(writePath);
        }
        return writeResult;
    }

    /**
     * Delete the data from the write test.
     * 
     */
    private void cleanupS3Write(String writePath) {
        if (writePath.startsWith("s3")) {
            BasicSessionCredentials awsCreds = new BasicSessionCredentials(config.getS3AccessKey(),
                    config.getS3SecretKey(), config.getS3SessionToken());
            AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                    .build();
            String bucketName = writePath.substring(writePath.indexOf("//"));
            StringBuilder bucketKey = new StringBuilder();
            String[] paths = bucketName.split("/");
            bucketName = paths[2];
            for (int i = 3; i < paths.length; i++) {
                if (i > 3) {
                    bucketKey.append("/");
                }
                bucketKey.append(paths[i]);
            }
            try {
                ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName)
                        .withPrefix(bucketKey.toString());
                ListObjectsV2Result listing = s3.listObjectsV2(req);
                for (S3ObjectSummary summary : listing.getObjectSummaries()) {
                    s3.deleteObject(new DeleteObjectRequest(bucketName, summary.getKey()));
                }
                s3.deleteObject(new DeleteObjectRequest(bucketName, bucketKey.toString()));
            } catch (SdkClientException e) {
                logger.error("exception caught while cleaning up s3 bucket: {}",(Object[])e.getStackTrace());
            }
        }
    }

    /**
     * Generate test data which will be used during the writeTest.
     * 
     * @return Dataset<Row>
     */
    private Dataset<Row> createTestData(SparkSession sparkSession) {
        List<Row> list = new ArrayList<>();
        list.add(RowFactory.create("one"));
        List<org.apache.spark.sql.types.StructField> listOfStructField = new ArrayList<>();
        listOfStructField.add(DataTypes.createStructField("test", DataTypes.StringType, true));
        StructType structType = DataTypes.createStructType(listOfStructField);
        return sparkSession.createDataFrame(list, structType);
    }
}
