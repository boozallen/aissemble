package com.boozallen.aissemble.messaging.python;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Messaging::Python::Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
