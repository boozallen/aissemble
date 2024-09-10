package com.boozallen.aissemble.upgrade.migration.utils;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.util.AissembleFileUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class AissembleFileUtilSteps {

    private Path testFile = Path.of("target/test.txt");
    private List<String> fetchedLines;

    @Given("a file with the contents:")
    public void aFileWithTheContents(String contents) throws IOException {
        Files.write(testFile, contents.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }

    @When("I retrieve the lines {int} to {int} of the file")
    public void iRetrieveTheLinesStartToEndOfTheFile(int start, int end) {
        fetchedLines = AissembleFileUtils.getLines(testFile, start, end);
    }

    @Then("the lines should be {string}")
    public void theLinesShouldBe(String expected) {
        List<String> expectedLines = expected.lines().collect(Collectors.toList());
        Assert.assertEquals("getLines fetched incorrect content", expectedLines, fetchedLines);
    }
}
