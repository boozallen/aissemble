package com.boozallen.aissemble.messaging.python;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Messaging::Python::Service
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

/**
 * Enum storing the various ack strategies; mirrors messaging client's ack strategy enum.
 */
public enum AckStrategy {
    POSTPROCESSING(0),
    MANUAL(1);

    private int value;

    /**
     * @param value the integer value of the ack strategy
     */
    AckStrategy(int value) {
        this.value = value;
    }

    /**
     * @return the integer value of this ack strategy
     */
    public int getIndex() {
        return value;
    }

    /**
     * @param value the integer value of the ack strategy
     * @return      the ack strategy that corresponds to the passed value argument
     * @throws IllegalArgumentException if the value specified is not a known ack strategy
     */
    public static AckStrategy valueOf(int value) throws IllegalArgumentException {
        for (AckStrategy ackStrategy : AckStrategy.values()) {
            if (ackStrategy.getIndex() == value) {
                return ackStrategy;
            }
        }
        throw new IllegalArgumentException("No AckStrategy with value " + value + " found");
    }
}
