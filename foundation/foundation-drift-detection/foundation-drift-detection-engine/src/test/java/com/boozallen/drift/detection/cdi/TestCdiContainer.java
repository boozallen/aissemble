package com.boozallen.drift.detection.cdi;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

public class TestCdiContainer extends DriftDetectionCdiContainer {

    public TestCdiContainer() {
        super();
        create(new TestCdiContext());
    }

}
