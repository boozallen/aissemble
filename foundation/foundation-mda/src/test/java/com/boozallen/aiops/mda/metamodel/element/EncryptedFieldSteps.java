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

    @Given("a record with a field specifying the column name {string} as secure")
    public void a_record_with_a_field_specifying_the_column_name_as_secure(String columnName) {
        RecordElement newRecord = createNewRecordWithNameAndPackage("ColumnTest", BOOZ_ALLEN_PACKAGE);
        RecordFieldElement field = createDefaultField("testColumn" + StringUtils.capitalize(columnName));
        field.setColumn(columnName);
        field.setSecurityPolicy("someEncryptID");
        newRecord.addField(field);
        saveRecordToFile(newRecord);
    }

    @When("encrypted records are read")
    public void encrypted_records_are_read() {
        readMetadata();

        Map<String, Record> records = metadataRepo.getRecords(recordPackageName);
        record = records.values().iterator().next();

        MessageTracker messageTracker = MessageTracker.getInstance();
        encounteredError = messageTracker.hasErrors();
    }

    @Then("the record field is available and encrypted with the encryption policy {string}")
    public void the_record_field_is_available_and_encrypted_with_the_encryption_policy(String expectedPolicy) {
        RecordField foundField = getAndValidateSingleField();
        assertEquals("Unexpected encryption policy for field '" + foundField.getName() + "'!", expectedPolicy,
                foundField.getSecurityPolicy());
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
