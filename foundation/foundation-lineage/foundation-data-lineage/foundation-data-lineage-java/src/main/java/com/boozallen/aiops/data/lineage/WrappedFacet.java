package com.boozallen.aiops.data.lineage;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */
public class WrappedFacet<T> extends Facet<T> {
    private T openLineageFacet;

    public WrappedFacet(T openLineageFacet) {
        this.openLineageFacet = openLineageFacet;
    }

    public T getOpenLineageFacet() {
        return openLineageFacet;
    }
}
