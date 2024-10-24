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

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.rest.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public abstract class AbstractFileStore {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractFileStore.class);
    private final String name;
    private FileStoreConfig fileStoreConfig;

    public AbstractFileStore(final String name) {
        this.name = name;
        fileStoreConfig = new EnvironmentVariableFileStoreConfig(name);
    }

    /**
     * Opens a connection to the file store.
     *
     * @return a fully connected context
     */
    public BlobStoreContext openContext() {
        ContextBuilder contextBuilder = ContextBuilder.newBuilder(fileStoreConfig.getProvider())
                .credentials(fileStoreConfig.getAccessKeyId(), fileStoreConfig.getSecretAccessKey());

        if (fileStoreConfig.getOverrides() != null) {
            contextBuilder.overrides(fileStoreConfig.getOverrides());
        }
        return contextBuilder.buildView(BlobStoreContext.class);
    }

    /**
     * Creates a container for the configured store
     *
     * @param container the container name
     * @return true if the container is created, false otherwise
     */
    public boolean createContainer(final String container) {
        try (BlobStoreContext context = openContext()) {
            BlobStore store = context.getBlobStore();
            if (!store.containerExists(container)) {
                return store.createContainerInLocation(null, container);
            }
        }
        return false;
    }

    /**
     * Stores the data from the input stream as a file on the file store.
     *
     * @param container the name of the container
     * @param location  the location/file name on the file store to write to
     * @param stream    the stream of data to write
     * @param size      the number of bytes to read from the stream
     * @return etag of the resulting blob on the file store, possibly null where etags are unsupported
     */
    public String store(final String container, final String location, final InputStream stream, final long size) {
        try (BlobStoreContext context = openContext()) {
            BlobStore store = context.getBlobStore();
            Blob blob = store.blobBuilder(location)
                    .payload(stream)
                    .contentLength(size)
                    .build();
            return store.putBlob(container, blob);
        }
    }

    /**
     * Uploads the local file to the file store.  Stores the file at the root of the container
     * with the same filename as the local file.
     *
     * @param container the container name to store the file in
     * @param localPath the path to the local file to upload
     * @return etag of the resulting blob on the file store, possibly null where etags are unsupported
     * @see #store(String, Path)
     */
    public String store(final String container, final Path localPath) {
        return store(container, localPath.getFileName().toString(), localPath);
    }

    /**
     * Uploads the local file to the file store.
     *
     * @param location  the location/file name on the file store to write to
     * @param localPath the path to the local file to upload
     * @return etag of the resulting blob on the file store, possibly null where etags are unsupported
     */
    public String store(final String container, final String location, final Path localPath) {
        try (final BlobStoreContext context = openContext()) {
            final BlobStore store = context.getBlobStore();
            final Blob blob = store.blobBuilder(location)
                    .payload(localPath.toFile())
                    .build();
            return store.putBlob(container, blob);
        }
    }

    /**
     * Uploads the blob to the file store.
     *
     * @param container the container
     * @param blob      the blob containing the information for uploading
     * @return etag of the blob you uploaded, possibly null where etags are unsupported
     */
    public String store(final String container, final Blob blob) {
        try (BlobStoreContext context = openContext()) {
            BlobStore store = context.getBlobStore();
            return store.putBlob(container, blob);
        }
    }

    /**
     * Fetches data from a file on the file store.
     *
     * @param container the container
     * @param location  the location of the file on the file store
     * @return an {@link InputStream} of the data
     * @throws IOException if the data stream cannot be opened
     */
    public InputStream fetch(final String container, final String location) throws IOException {
        try (BlobStoreContext context = openContext()) {
            BlobStore store = context.getBlobStore();
            Blob blob = store.getBlob(container, location);
            if (blob == null) {
                throwBlobNotFound(container, location);
            }
            return blob.getPayload().openStream();
        }
    }

    /**
     * Downloads a file from the file store to the local file system.
     *
     * @param container the container
     * @param location  the location of the file on the file store
     * @param localPath the local file to write to
     * @throws IOException if the operation fails to write to the file
     */
    public void fetch(final String container, final String location, final Path localPath) throws IOException {
        try (BlobStoreContext context = openContext()) {
            BlobStore store = context.getBlobStore();
            Blob blob = store.getBlob(container, location);
            if (blob == null) {
                throwBlobNotFound(container, location);
            }
            try (InputStream blobInput = blob.getPayload().openStream();
                 OutputStream fileOutput = Files.newOutputStream(localPath, CREATE, TRUNCATE_EXISTING)) {
                byte[] buffer = new byte[1024 * 8];
                int l;
                while ((l = blobInput.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, l);
                }
            }
        }
    }

    /**
     * Returns the full URI of a file on the file store.
     *
     * @param container the container
     * @param location  the location of the file on the file store
     * @return the URI of the file
     */
    public URI getUri(final String container, final String location) {
        try (BlobStoreContext context = openContext()) {
            BlobStore store = context.getBlobStore();
            BlobMetadata metadata = store.blobMetadata(container, location);
            if (metadata == null) {
                throwBlobNotFound(container, location);
            }
            return metadata.getUri();
        }
    }

    /**
     * Checks if a file exists on the file store.
     *
     * @param container the container
     * @param location  the location of the file on the file store
     * @return true if there is a file at the given location
     */
    public boolean fileExists(final String container, final String location) {
        try (BlobStoreContext context = openContext()) {
            BlobStore store = context.getBlobStore();
            return store.blobExists(container, location);
        }
    }

    private void throwBlobNotFound(final String container, final String location) {
        throw new ResourceNotFoundException("File does not exists on file store in container '" + container + "': " + location);
    }

    /**
     * Gets the provided fileStoreConfig. Defaults to environmentVariableStoreConfig.
     *
     * @return the fileStoreConfig
     */
    public FileStoreConfig getFileStoreConfig() {
        return this.fileStoreConfig;
    }

    /**
     * Sets the fileStoreConfig to use.
     *
     * @param fileStoreConfig the filestore config
     */
    public void setFileStoreConfig(final FileStoreConfig fileStoreConfig) {
        this.fileStoreConfig = fileStoreConfig;
    }
}
