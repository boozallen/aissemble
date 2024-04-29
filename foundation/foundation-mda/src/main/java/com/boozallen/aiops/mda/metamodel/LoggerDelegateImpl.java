package com.boozallen.aiops.mda.metamodel;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.technologybrewery.fermenter.mda.GenerateSourcesHelper.LoggerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log via slf4j.
 */
public class LoggerDelegateImpl implements LoggerDelegate {

    private static final Logger logger = LoggerFactory.getLogger(LoggerDelegateImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(LogLevel level, String message) {
        switch (level) {
        case TRACE:
        case DEBUG:
            logger.debug(message);
            break;
        case INFO:
            logger.info(message);
            break;
        case WARN:
            logger.warn(message);
            break;
        case ERROR:
            logger.error(message);
            break;
        default:
            logger.info(message);
            break;
        }
    }

}
