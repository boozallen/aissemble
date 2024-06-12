package com.boozallen.aissemble.upgrade.util.pom;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputSource;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.technologybrewery.baton.BatonException;

import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.Writer;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class PomHelper {
    /**
     * Helper function to construct a Model of a pom file.
     * @param file File object representing a pom file.
     * @return the constructed Model.
     */
    public static Model getLocationAnnotatedModel(File file) {
        LocationAwareMavenReader reader = new LocationAwareMavenReader();
        InputSource source = new InputSource();
        source.setLocation(file.getAbsolutePath());
        try {
            return reader.read(new FileReader(file), true, source);
        } catch (IOException | XmlPullParserException e) {
            throw new BatonException("Unable to parse pom file at path: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Writes a finalized set of modifications back to a pom file.
     * @param file The original pom file.
     * @param modifications Finalized set of modifications.
     * @return true iff the write operation completes successfully.
     */
    public static boolean writeModifications(File file, PomModifications.Final modifications) {
        String tempFile = file.getAbsolutePath() + ".tmp";
        try (Writer out = new FileWriter(tempFile);
             BufferedReader in = new BufferedReader(new FileReader(file))) {
            int lineNumber = 0;
            String line;
            while ((line = in.readLine()) != null) {
                lineNumber++; // MavenXpp3ReaderEx uses 1-based line numbers
                if (modifications.appliesTo(lineNumber)) {
                    lineNumber = modifications.apply(in, out, line);
                } else {
                    out.write(line);
                    out.write("\n");
                }
            }
            in.close();
            out.close();
            Files.move(Paths.get(tempFile), Paths.get(file.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BatonException("Failed to update file:" + file.getPath(), e);
        }
        return true;
    }

    public static InputLocation incrementColumn(InputLocation location, int i) {
        return new InputLocation(location.getLineNumber(), location.getColumnNumber() + i, location.getSource());
    }
}
