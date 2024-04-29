package com.boozallen.aiops.metadata.hive;

/*-
 * #%L
 * AIOps Docker Baseline::AIOps Metadata Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.core.metadata.MetadataAPI;
import com.boozallen.aiops.core.metadata.MetadataModel;
import com.boozallen.aiops.metadata.MetadataAPIType;
import com.boozallen.aiops.metadata.hive.config.HiveMetadataConfig;
import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.commons.collections.MapUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.TableIdentifier;
import org.apache.spark.sql.catalyst.analysis.NoSuchDatabaseException;
import org.apache.spark.sql.catalyst.analysis.NoSuchTableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.sql.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.next_day;

/**
 * Class to persist metadata to a Hive database. Requires a Spark Context with Hive
 * support enabled. Configurations located in MetadataConfig.
 */
@ApplicationScoped
@MetadataAPIType("hive")
public class HiveMetadataAPIService implements MetadataAPI {
    private static final Logger logger = LoggerFactory.getLogger(HiveMetadataAPIService.class);
    private static final HiveMetadataConfig metadataConfig = KrauseningConfigFactory.create(HiveMetadataConfig.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void createMetadata(final MetadataModel metadata) {
            final SparkSession session = getSparkSession();
            final Encoder<MetadataModel> encoder = Encoders.bean(MetadataModel.class);
            final Dataset<MetadataModel> ds = session.createDataset(Arrays.asList(metadata), encoder);

            ds.write()
                    .format(metadataConfig.tableFormat())
                    .mode(SaveMode.Append)
                    .saveAsTable(metadataConfig.tableName());
            logger.info("Metadata saved");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MetadataModel> getMetadata(final Map<String, Object> searchParams) {
        Dataset<Row> metadata = getSparkSession().read()
                .table(metadataConfig.tableName())
                .orderBy("timestamp");

        if (MapUtils.isNotEmpty(searchParams)) {
            for (Map.Entry<String, Object> param : searchParams.entrySet()) {
                String key = param.getKey();
                Object value = param.getValue();
                metadata = metadata.filter(col(key).equalTo(value));
            }
        }

        // The collectAsList method in convertToMetadataModelList throws an inconsistent error
        // The try/catch is used to gracefully handle the exception and provide debug logs
        List<MetadataModel> metadataModelList = new ArrayList<>();
        try {
            metadataModelList = convertToMetadataModelList(metadata);
        } catch (RuntimeException ex) {
            logger.info("An exception was thrown when converting a DataSet of Rows to a list of MetadataModels", ex);
            try {
                logger.debug(String.valueOf(getSparkSession().sessionState().catalog().getTableMetadata(new TableIdentifier(metadataConfig.tableName()))));
            } catch (NoSuchTableException | NoSuchDatabaseException logException) {
                logger.error("An exception was thrown when retrieving metadata for table " + metadataConfig.tableName(), logException);
            }
        }
        return metadataModelList;
    }

    private List<MetadataModel> convertToMetadataModelList(Dataset<Row> dataset) {
        List<MetadataModel> metadata = new ArrayList<>();
        for (Row row : dataset.collectAsList()) {
            String resource = row.getString(row.fieldIndex("resource"));
            String subject = row.getString(row.fieldIndex("subject"));
            String action = row.getString(row.fieldIndex("action"));
            Timestamp timestamp = row.getTimestamp(row.fieldIndex("timestamp"));
            Map<String, String> additionalValues = row.getJavaMap(row.fieldIndex("additionalValues"));

            metadata.add(new MetadataModel(resource, subject, action, timestamp.toInstant(), additionalValues));
        }

        return metadata;
    }

    public SparkSession getSparkSession() {
        return SparkSession.builder()
                .master("local[*]")
                .appName(metadataConfig.sparkAppName())
                .enableHiveSupport()
                .config("spark.driver.host", "localhost")
                .getOrCreate();
    }
}
