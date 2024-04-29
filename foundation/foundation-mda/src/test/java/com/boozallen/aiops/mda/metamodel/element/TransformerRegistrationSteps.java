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

import java.util.Map;

import io.cucumber.java.DataTableType;

public class TransformerRegistrationSteps extends AbstractModelInstanceSteps {

    @DataTableType
    public DictionaryTypeElement defineDictionaryTypeElement(Map<String, String> entry) {
        DictionaryTypeElement dictionaryType = createDictionaryType(entry.get("name"), entry.get("simpleType"));
        
        dictionaryType.setEthicsPolicy(entry.get("ethicsPolicy"));
        dictionaryType.setProtectionPolicy(entry.get("protectionPolicy"));
        dictionaryType.setDriftPolicy(entry.get("driftPolicy"));
        
        return dictionaryType;
    }
}
