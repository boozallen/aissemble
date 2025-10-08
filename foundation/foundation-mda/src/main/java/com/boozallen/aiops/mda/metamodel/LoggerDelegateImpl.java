package com.boozallen.aiops.mda.metamodel;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
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
