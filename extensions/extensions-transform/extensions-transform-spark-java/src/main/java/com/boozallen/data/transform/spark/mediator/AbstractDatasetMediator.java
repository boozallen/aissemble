package com.boozallen.data.transform.spark.mediator;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Transform::Spark::Java
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

import com.boozallen.data.transform.DataTransformException;
import org.apache.spark.sql.Dataset;
import org.technologybrewery.mash.Mediator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * {@link AbstractDatasetMediator} class represents a {@link Mediator} for
 * performing transformations on a Spark dataset.
 *
 * @param <T> The type of the Spark Dataset's records
 * @author Booz Allen Hamilton
 */
public abstract class AbstractDatasetMediator<T> extends Mediator {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDatasetMediator.class);

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Object performMediation(Object input, Properties properties) {
        Object output = null;

        if (input != null) {
            validate(input, properties);
            output = transform((Dataset<T>) input, properties);
        } else {
            logger.warn("Input dataset is null - skipping transformation");
        }

        return output;
    }

    /**
     * Validates the input and properties for this transformer.
     *
     * @param input      the input for this transformer
     * @param properties the properties for this transformer
     */
    public void validate(Object input, Properties properties) {
        if (!(input instanceof Dataset)) {
            throw new DataTransformException("Input (" + input.getClass().getName() + ") is not a Spark dataset");
        }
    }

    /**
     * Transforms the input dataset.
     *
     * @param input      the dataset to transform
     * @param properties the properties for this transformer
     * @return
     */
    public abstract Dataset<T> transform(Dataset<T> input, Properties properties);

}
