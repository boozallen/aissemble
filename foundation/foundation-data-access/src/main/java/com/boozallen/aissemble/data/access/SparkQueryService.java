package com.boozallen.aissemble.data.access;

/*-
 * #%L
 * aiSSEMBLE Foundation::aiSSEMBLE Data Access
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aissemble.data.access.config.DataAccessConfig;

import io.agroal.api.AgroalDataSource;

/**
 * Service to query the database for records.
 */
@ApplicationScoped
public class SparkQueryService implements QueryService {

    private static final Logger logger = LoggerFactory.getLogger(SparkQueryService.class);

    private static DataAccessConfig config = KrauseningConfigFactory.create(DataAccessConfig.class);

    @Inject
    AgroalDataSource dataSource;

    /**
     * Executes a query to find all records from the given table.
     * 
     * @param table
     *            the table to query
     * @param recordType
     *            the type of record to map the query result sets to
     * @return the list of records returned from the query
     */
    public List<? extends AbstractDataAccessRecord> executeQuery(String table,
            Class<? extends AbstractDataAccessRecord> recordType) {

        int limit = config.defaultQuerySize();
        return executeQuery(table, limit, recordType);
    }

    public List<? extends AbstractDataAccessRecord> executeQuery(String table, DataAccessWhereClause[] where,
                                                                 Class<? extends AbstractDataAccessRecord> recordType) {
        int limit = config.defaultQuerySize();
        return executeQuery(table, where, limit, recordType);
    }

    public List<? extends AbstractDataAccessRecord> executeQuery(String table, Integer limit,
                                                                 Class<? extends AbstractDataAccessRecord> recordType) {
        return executeQuery(table, null, limit, recordType);
    }

    public List<? extends AbstractDataAccessRecord> executeQuery(String table, DataAccessWhereClause[] where, Integer limit,
                                                                 Class<? extends AbstractDataAccessRecord> recordType) {

        logger.info("Executing query on table {}...", table);
        logger.info("{} records will be returned", limit);

        final List<AbstractDataAccessRecord> results = new ArrayList<>();

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {

            // https://hadoopsters.com/2018/02/04/how-random-sampling-in-hive-works-and-how-to-use-it/
            final String query = buildQuery(table, where, limit);

            logger.info("Query was built as: {}", query);

            final ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                AbstractDataAccessRecord record = recordType.newInstance();
                record.mapResultSet(resultSet);
                results.add(record);
            }

            logger.info("Query returned {} results", results.size());
        } catch (Exception e) {
            logger.error("Error executing query", e);
        }

        return results;
    }

    private String buildQuery (String table, DataAccessWhereClause[] where, Integer limit) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select * from ");
        stringBuilder.append(table);
        stringBuilder.append(" ");

        if (ArrayUtils.isNotEmpty(where)) {
            stringBuilder.append(" where ");

            for (DataAccessWhereClause whereClause : where) {
                stringBuilder.append(whereClause.getFieldName());
                stringBuilder.append(" ");
                stringBuilder.append(getWhereOperatorString(whereClause.getOperator()));
                stringBuilder.append(" ");
                stringBuilder.append(whereClause.getValue());
                stringBuilder.append(" ");
            }
        }

        stringBuilder.append("limit ");
        stringBuilder.append(limit);

        return stringBuilder.toString();
    }

    private String getWhereOperatorString(DataAccessWhereOperator whereOperator) {
        switch (whereOperator) {
            case EQUALS:
                return "=";
            case NOT_EQUALS:
                return "<>";
            case LESS_THAN:
                return "<";
            case GREATER_THAN:
                return ">";
            case IN:
                return "IN";
            default:
                throw new RuntimeException("An unrecognized where clause operator was encountered.");
        }
    }

}
