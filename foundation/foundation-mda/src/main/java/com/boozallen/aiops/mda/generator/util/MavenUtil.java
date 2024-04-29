package com.boozallen.aiops.mda.generator.util;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.nio.file.Files;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import com.boozallen.aiops.mda.generator.util.SemanticDataUtil.DataRecordModule;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.LoggerFactory;


import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


public class MavenUtil {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MavenUtil.class);
    /**
     * Returns true if a file (or directory) with the name can be found.
     *
     * @param directory the directory search
     * @param name      the name to search for
     * @return true if the file or directory exists, false otherwise
     */
    public static boolean fileExists(final File directory, final String name) {
        File[] files = directory.listFiles();
        if (files != null) {
            return Arrays.stream(files)
                    .anyMatch(file -> file.getName().equals(name));
        }
        return false;
    }
    public static boolean isMavenDir(final File directory) {
        return fileExists(directory, "pom.xml");
    }


    /**
     * @deprecated
     * This method should no longer be used to find root project directory.
     * Use context.getExecutionRootDirectory() to find the root project directory. 
     */

    @Deprecated
    public static Optional<File> findRootProjectDirectory(final File directory) {
        Optional<File> result;
        File[] files;
        files = directory.listFiles();
        final File parent = directory.getParentFile();

        if (files == null) {
            // Empty Directory
            if (parent == null) {
                return Optional.empty();
            }
            result = findRootProjectDirectory(parent);
        } else if (isMavenDir(parent)) {
            // Multi-module project where parent exists
            result = findRootProjectDirectory(parent);
            if (!result.isPresent()) {
                return Optional.of(directory);
            }
        } else {
            // Either at the root of a Maven project or in an invalid directory (e.g. in src/main/resources or
            // completely outside a Maven project)
            result = isMavenDir(directory) ? Optional.of(directory) : Optional.empty();
        }

        return result;
    }

    public static String getPipelineModelModuleName(final File rootProjectDirectory) {
        return getChildModuleName(rootProjectDirectory, ".+-pipeline-models");
    }

    public static String getDeployModuleName(final File rootProjectDirectory) {
        return getChildModuleName(rootProjectDirectory, ".+-deploy");
    }

    public static String getDockerModuleName(final File rootProjectDirectory) {
        return getChildModuleName(rootProjectDirectory, ".+-docker");
    }

    public static String getSharedModuleName(final File rootProjectDirectory) {
        return getChildModuleName(rootProjectDirectory, ".+-shared");
    }

    /**
     * Discovers the appropriate data record module for the given language and data module type. If the language is not
     * the default language for the data module, a language-specific module is returned.
     *
     * @param context the generation context
     * @param metadataContext the metadata context
     * @param language the implementation language of the desired module
     * @param dataModule the type of the desired module
     * @return the name of the appropriate data record module
     */
    public static String getDataRecordModuleName(GenerationContext context, String metadataContext, Language language, DataRecordModule dataModule) {
        String projectPrefix = context.getRootArtifactId();
        String defaultDataRecords = projectPrefix + "-" + dataModule.getBaseName();
        Path projectRoot = context.getExecutionRootDirectory().toPath();
        Path defaultModulePath = projectRoot.resolve(Path.of(getSharedModuleName(context.getExecutionRootDirectory()), defaultDataRecords));
        Language defaultLanguage = getModuleLanguage(defaultModulePath.resolve("pom.xml"));
        if (SemanticDataUtil.arePythonDataRecordsNeeded(metadataContext)
                && SemanticDataUtil.areJavaDataRecordsNeeded(metadataContext)
                && !language.equals(defaultLanguage)) {
            return defaultDataRecords + "-" + language.getLanguageName();
        } else {
            return defaultDataRecords;
        }
    }

    public static String getChildModuleName(final File rootDirectory, final String childRegex) {

        File pomFile = new File(rootDirectory.getPath() + File.separator + "pom.xml");

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            
            Document doc = dBuilder.parse(pomFile);
            doc.getDocumentElement().normalize();
            //Get all fields with tag <module>
            NodeList nodeList = doc.getElementsByTagName("module");
            //Iterate through the list and get deploy-module name
            for (int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    String field = element.getTextContent();
                    if (field.matches(childRegex)){
                        return field;
                    }
                }
            }
        } catch (Exception e){
            logger.error("Unable to find deploy-module", e);
            throw new NoSuchElementException("No deploy module identified within " + pomFile.getPath());
        }
        return null;
    }

    /**
     * Reads the given POM file to determine the language of the module based on the packaging.
     *
     * @param pomFile the POM to read
     * @return the language of the module or UNKNOWN if the POM does not exist
     */
    public static Language getModuleLanguage(Path pomFile) {
        Language language = Language.UNKNOWN;
        if(Files.exists(pomFile)) {
            try {
                MavenXpp3Reader reader = new MavenXpp3Reader();
                Model model = reader.read(Files.newBufferedReader(pomFile));
                String existingPackaging = model.getPackaging();
                for (Language lang : Language.values()) {
                    if (existingPackaging.equals(lang.getPackaging())) {
                        language = lang;
                        break;
                    }
                }
            } catch (IOException | XmlPullParserException e) {
                throw new RuntimeException("Failed to read existing data record module to detect language", e);
            }
        }
        return language;
    }

    public enum Language {
        JAVA("java", "jar"),
        PYTHON("python", "habushu"),
        UNKNOWN("unknown", null);

        private final String languageName;
        private final String packaging;

        Language(String languageName, String packaging) {
            this.languageName = languageName;
            this.packaging = packaging;
        }

        public String getLanguageName() {
            return languageName;
        }

        public String getPackaging() {
            return packaging;
        }
    }
}


