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

import com.boozallen.aiops.mda.metamodel.json.AissembleMdaJsonUtils;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.util.MessageTracker;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EncryptedFieldSteps extends AbstractModelInstanceSteps {

    protected String recordPackageName;
    protected Record record;
    protected boolean encounteredError;

    @Before("@encryptfield")
    public void setUpObjectMapper() throws Exception {
        AissembleMdaJsonUtils.configureCustomObjectMappper();

        MessageTracker messageTracker = MessageTracker.getInstance();
        messageTracker.clear();

        FileUtils.deleteDirectory(GENERATED_METADATA_DIRECTORY);
    }

    @Given("the following dictionary types are defined:")
    public void the_following_dictionary_types_are_defined(List<DictionaryTypeElement> dictionaryTypes) throws Exception {
        createSampleDictionary(dictionaryTypes);
    }

    private RecordElement createNewRecordWithNameAndPackage(String name, String packageName) {
        RecordElement newRecord = new RecordElement();
        if (StringUtils.isNotBlank(name)) {
            newRecord.setName(name);
        }

        if (StringUtils.isNotBlank(packageName)) {
            newRecord.setPackage(packageName);
            recordPackageName = packageName;

        } else {
            recordPackageName = BOOZ_ALLEN_PACKAGE;

        }

        return newRecord;
    }

    private RecordField getAndValidateSingleField() {
        List<RecordField> foundFields = record.getFields();
        assertEquals("Unexpected number of  fields found!", 1, foundFields.size());
        RecordField foundField = foundFields.iterator().next();
        return foundField;
    }

    private RecordFieldElement createDefaultField(String fieldName) {
        RecordFieldElement field = new RecordFieldElement();
        field.setName(fieldName);
        RecordFieldTypeElement type = createDefaultFieldType();
        field.setType(type);
        return field;
    }

    private RecordFieldTypeElement createDefaultFieldType() {
        RecordFieldTypeElement type = new RecordFieldTypeElement();
        type.setPackage(BOOZ_ALLEN_PACKAGE);
        type.setName("ssn");
        return type;
    }
}
