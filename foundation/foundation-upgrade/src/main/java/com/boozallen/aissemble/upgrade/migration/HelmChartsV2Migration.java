package com.boozallen.aissemble.upgrade.migration;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.technologybrewery.baton.util.FileUtils;
import org.technologybrewery.baton.util.CommonUtils;
import static org.technologybrewery.baton.util.CommonUtils.isLessThanVersion;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HelmChartsV2Migration extends AbstractAissembleMigration {
    public static final Logger logger = LoggerFactory.getLogger(HelmChartsV2Migration.class);
    public static final String VERSION_APP_VERSION_REPLACE_REGEX = "([\\t -]+ (?:version|appVersion): *)(.*)";
    private HashMap<String,Object> yamlMap;

    private InputStream fileStream;

    private String mavenAissembleVersion;

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        Yaml helmChartYaml = new Yaml();
        boolean shouldExecute = false;

        if (file != null && file.exists()) {
            try {
                fileStream = Files.asByteSource(file).openStream();
                yamlMap = helmChartYaml.load(fileStream);
                if (isAissembleChartFile(yamlMap)) {
                    // get current app version from pom for comparison
                    mavenAissembleVersion = getAissembleVersion();
                    String apiVersion = (String) yamlMap.get("apiVersion");

                    if (mavenAissembleVersion != null && StringUtils.equals(apiVersion, "v2")) {
                        List<HashMap<String, Object>> dependencies = (List<HashMap<String, Object>>) yamlMap.get("dependencies");
                        if (dependencies != null) {
                            shouldExecute = dependencies.stream().anyMatch(dependency -> {
                                Object chartAissembleVersion = dependency.get("version");
                                if (chartAissembleVersion instanceof String) {
                                    return isLessThanVersion((String) chartAissembleVersion, mavenAissembleVersion);
                                }
                                return false;
                            });
                        }
                    } else {
                        logger.error("Unable to parse version from current project");
                    }
                }
            } catch (IOException e) {
                logger.error("Unable to load file into yaml class due to exception:", e);
            }
        }
        if (shouldExecute) {
            logger.info("Found v2 Helm Chart with dependencies that are upgrade candidates");
        }
        return shouldExecute;
    }

    @Override
    protected boolean performMigration(File file) {
        boolean performedSuccessfully = false;

        ArrayList<String> newFileContents = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean startChangingVersions = false;
            boolean lastNameWasAissemble = false;

            while ((line = reader.readLine()) != null) {
                String newLine;

                startChangingVersions = disableIfNotDependencyProperty(startChangingVersions, line);

                if (!startChangingVersions && line.contains("dependencies:")) {
                    startChangingVersions = true;
                }

                if (startChangingVersions && line.contains("name:")) {
                    lastNameWasAissemble = line.contains("aissemble-");
                }

                if (startChangingVersions && lastNameWasAissemble && line.matches(VERSION_APP_VERSION_REPLACE_REGEX)) {
                    newLine = upgradeVersionInString(line);
                } else {
                    newLine = StringUtils.appendIfMissing(line, "\n");
                }

                newFileContents.add(newLine);
            }

            writeNewFile(file, newFileContents);
            performedSuccessfully = true;
            } catch (Exception e) {
                logger.error("Unable to perform helm chart v2 migration", e);
            }
        return performedSuccessfully;
    }

    private static void writeNewFile(File file, ArrayList<String> newFileContents) throws IOException {
        OutputStream fileStream = new FileOutputStream(file);
        for (String content : newFileContents) {
            fileStream.write(content.getBytes(StandardCharsets.UTF_8));
        }
        fileStream.flush();
        fileStream.close();
    }

    private String upgradeVersionInString(String line) {
        String newLine;
        List<String> captureGroups = FileUtils.getRegExCaptureGroups(
                VERSION_APP_VERSION_REPLACE_REGEX,
                line
        );
        String currentVersion = getAissembleVersion();
        String oldVersion = captureGroups.get(1).replaceAll("['|\"]", "").trim();
        String newVersion = CommonUtils.isLessThanVersion(oldVersion, currentVersion) ? currentVersion : oldVersion;
        newLine = captureGroups.get(0) + "\"" + newVersion + "\"";
        return StringUtils.appendIfMissing(newLine,"\n");
    }

    private static boolean disableIfNotDependencyProperty(boolean startChangingVersions, String line) {
        if (startChangingVersions
                && !line.startsWith(" ")
                && !line.startsWith("\t")
                && !line.startsWith("-")
                && !line.startsWith("#")) {
            startChangingVersions = false;
        }
        return startChangingVersions;
    }

    private static boolean isAissembleChartFile(HashMap<String, Object> map) {
        Object yamlName = map.get("name");
        return yamlName instanceof String;
    }
}
