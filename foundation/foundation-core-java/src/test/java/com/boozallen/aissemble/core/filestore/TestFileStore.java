package com.boozallen.aissemble.core.filestore;

/*-
 * #%L
 * aiSSEMBLE Foundation::aiSSEMBLE Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */
import org.jclouds.filesystem.reference.FilesystemConstants;

import javax.enterprise.context.ApplicationScoped;
import java.nio.file.Paths;
import java.util.Properties;

@ApplicationScoped
public class TestFileStore extends AbstractFileStore {
    private static final String BASE_DIR = "target/file-store";
    public static final String CONTAINER = "test-container";

    public TestFileStore() {
        super("TestFile");
        // Configure the FileStoreConfig with these values
        final Properties overrides = new Properties();
        overrides.setProperty(FilesystemConstants.PROPERTY_BASEDIR, BASE_DIR);

        final MockFileStoreConfig mock = new MockFileStoreConfig("test");
        mock.setProvider("filesystem");
        mock.setOverrides(overrides);

        setFileStoreConfig(mock);

    }

    public static String getStoreRoot() {
        return Paths.get(BASE_DIR, CONTAINER).toString();
    }
}
