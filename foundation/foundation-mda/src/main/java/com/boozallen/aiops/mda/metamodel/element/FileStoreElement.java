package com.boozallen.aiops.mda.metamodel.element;

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
