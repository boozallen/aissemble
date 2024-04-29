package com.boozallen.aiops.core;

/*-
 * #%L
 * AIOps Foundation::AIOps Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.core.filestore.TestFileStore;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class StorageSteps {
    private String storeRoot = TestFileStore.getStoreRoot();
    private String localRoot = "target/file-local";
    private String fetchRoot = "target/file-fetch";
    private TestFileStore fileStore;
    private String fileName;
    private Path localFile;
    private Path remoteFile;
    private Path fetchedFile;
    private boolean existsResult;

    @Before("@fileStore")
    public void cleanup() throws IOException {
        Files.createDirectories(Paths.get(storeRoot));
        Files.createDirectories(Paths.get(localRoot));
        Files.createDirectories(Paths.get(fetchRoot));
    }

    @Given("^a local file exists$")
    public void aLocalFileExists() throws IOException {
        fileName = "test-file-" + RandomStringUtils.randomAlphanumeric(5);
        localFile = Paths.get(localRoot, fileName);
        Files.write(localFile, getRandomFileData());
    }

    @Given("a remote file exists")
    public void aRemoteFileExists() throws IOException {
        fileName = "test-file-" + RandomStringUtils.randomAlphanumeric(5);
        remoteFile = Paths.get(storeRoot, fileName);
        Files.write(remoteFile, getRandomFileData());
    }

    @Given("a storage provider is created")
    public void aStorageProviderIsCreated() {
        fileStore = new TestFileStore();
        fileStore.createContainer(TestFileStore.CONTAINER);
    }

    @Given("a remote file {string}")
    public void aRemoteFile(String exists) throws IOException {
        if("exists".equals(exists)) {
            aRemoteFileExists();
        } else {
            fileName = "not-extant";
        }
    }

    @When("I store the file")
    public void iStoreTheFile() {
        fileStore.store(TestFileStore.CONTAINER, fileName, localFile);
        remoteFile = Paths.get(storeRoot, fileName);
    }

    @When("I retrieve the file")
    public void iRetrieveTheFile() throws IOException {
        fetchedFile = Paths.get(fetchRoot, fileName);
        fileStore.fetch(TestFileStore.CONTAINER, fileName, fetchedFile);
    }

    @When("I check for the file remotely")
    public void iCheckForTheFileRemotely() {
        existsResult = fileStore.fileExists(TestFileStore.CONTAINER, fileName);
    }

    @Then("the file is available remotely")
    public void theFileIsAvailableRemotely() throws IOException {
        compareFiles(this.remoteFile, this.localFile);
    }

    @Then("the file is available locally")
    public void theFileIsAvailableLocally() throws IOException {
        compareFiles(this.fetchedFile, this.remoteFile);
    }

    @Then("the storage provider returns {string}")
    public void theStorageProviderReturns(String existsString) {
        boolean expectedResult = Boolean.parseBoolean(existsString);
        Assert.assertEquals("exists check returned wrong value", expectedResult, existsResult);
    }

    private byte[] getRandomFileData() {
        return RandomStringUtils.randomAscii(1024).getBytes(StandardCharsets.UTF_8);
    }

    private void compareFiles(Path newFile, Path existingFile) throws IOException {
        Assert.assertTrue("The file is not present in the file store: " + fileName, Files.exists(newFile));
        List<String> newBytes = Files.readAllLines(newFile);
        List<String> existingBytes = Files.readAllLines(existingFile);
        Assert.assertEquals("The content in the file store does not match the local file", existingBytes, newBytes);
    }
}
