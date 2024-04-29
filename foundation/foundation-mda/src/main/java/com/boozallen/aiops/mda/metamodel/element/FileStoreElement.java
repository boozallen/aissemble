package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.metamodel.element.MetamodelElement;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileStoreElement extends MetamodelElement implements FileStore {
    private static final Predicate<String> validName = Pattern.compile("[A-Za-z0-9]+").asMatchPredicate();

    public FileStoreElement() {
    }

    /**
     * Configure a FileStore
     * @param name the name of the file store
     */
    public FileStoreElement(String name) {
        this.name = name;
    }

    @Override
    public void validate() {
        super.validate();
        if (!validName.test(name)) {
            String suggestion = Arrays.stream(name.split("[^A-Za-z0-9]+"))
                    .sequential()
                    .map(StringUtils::lowerCase)
                    .map(StringUtils::capitalize)
                    .collect(Collectors.joining(""));
            messageTracker.addErrorMessage("File store name is not in Pascal Case: " + name + " (try " + suggestion + ")");
        }
    }

}
