package com.boozallen.aissemble.upgrade.migration.v1_7_0;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */
import com.boozallen.aissemble.upgrade.migration.AbstractHelmNameMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

/**
 * Baton migration used to migrate the Chart.yaml and values*.yaml files within a project's deploy folder
 * to use the new Helm chart naming convention (aissemble-<chart-name>-chart)
 * Example: aissemble-kafka to aissemble-kafka-chart
 */

public class HelmChartsNameMigration extends AbstractHelmNameMigration {
    public static final Logger logger = LoggerFactory.getLogger(HelmChartsNameMigration.class);
    private static final String dependencyNameReplaceRegex = "(aissemble-[^\\/\\n:]+)(?<!-chart)$";
    private static final String appNameReplaceRegex = "(^\\s*)(aissemble-[^\\/\\n:]+)(?<!-chart):$";
    private static final String COLON = ":";
    private static final String SECOND_REGEX_GROUPING = "$2";
    private static final String CHART_POSTFIX = "-chart";

    public String getDependencyNameReplacementText(){
        return FIRST_REGEX_GROUPING.concat(CHART_POSTFIX);
    }

    public String getAppNameReplacementText(){
        return FIRST_REGEX_GROUPING.concat(SECOND_REGEX_GROUPING + CHART_POSTFIX + COLON);
    }

    @Override
    public String getReplacementRegex(File file) {
        if (file.getName().matches("Chart.yaml")){
            return dependencyNameReplaceRegex;
        } else {
            return appNameReplaceRegex;
        }
    }

    @Override
    public String getReplacementText(File file){
        if (file.getName().matches("Chart.yaml")){
            return getDependencyNameReplacementText();
        } else {
            return getAppNameReplacementText();
        }
    }
}


