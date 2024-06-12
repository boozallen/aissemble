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

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import com.boozallen.aissemble.upgrade.util.pom.PomHelper;
import com.boozallen.aissemble.upgrade.util.pom.PomModifications;
import org.apache.maven.model.Build;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Upgrades pipeline POM files to replace the old aissemble-spark-application chart template execution with the updated
 * execution that uses the ghcr.io repository.  These executions are used to create the SparkApplication files that can
 * be executed/submitted by Airflow.  However, they are included in all pipelines regardless of whether Airflow is in
 * use.
 */
public class SparkAppExecMigration extends AbstractAissembleMigration {

    public static final String NEW_CHART = "${aissemble.helm.repo.protocol}://${aissemble.helm.repo}/aissemble-spark-application-chart";

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        Model pom = PomHelper.getLocationAnnotatedModel(file);
        return !getSparkAppExecutions(pom).isEmpty();
    }

    @Override
    protected boolean performMigration(File file) {
        Model pom = PomHelper.getLocationAnnotatedModel(file);
        List<PluginExecution> execs = getSparkAppExecutions(pom);
        PomModifications modifications = new PomModifications();
        for (PluginExecution exec : execs) {
            modifyExecution(exec, modifications);
        }
        return PomHelper.writeModifications(file, modifications.finalizeMods());
    }

    /**
     * Modifies the exec-maven-plugin execution to use the new aissemble-spark-application chart template format.
     * @param exec the plugin <execution> to modify
     * @param modifications accumulator to which modifications are added
     */
    private void modifyExecution(PluginExecution exec, PomModifications modifications) {
        Xpp3Dom[] args = getConfig(exec, "arguments").getChildren("argument");
        for (Xpp3Dom arg : args) {
            InputLocation location = (InputLocation) arg.getInputLocation();
            String value = arg.getValue();
            if ("aissemble-spark-application".equals(value)) {
                InputLocation end = PomHelper.incrementColumn(location, "aissemble-spark-application".length());
                modifications.add(new PomModifications.Replacement(location, end, NEW_CHART));
            } else if ("--repo".equals(value) || "${aissemble.helm.repo}".equals(value)) {
                InputLocation start = PomHelper.incrementColumn(location, -"<argument>".length());
                InputLocation end = PomHelper.incrementColumn(location, value.length() + "</argument>".length());
                modifications.add(new PomModifications.Deletion(start, end));
            }
        }
    }

    /**
     * Gets the plugin <execution> blocks which use the exec-maven-plugin to run helm template on the aissemble-spark-application chart.
     * @param pom the POM to search
     * @return the executions that need to be modified
     */
    private List<PluginExecution> getSparkAppExecutions(Model pom) {
        Build build = pom.getBuild();
        if (build == null) {
            return List.of();
        }
        List<Plugin> plugins = build.getPlugins();
        if (plugins == null) {
            return List.of();
        }
        return plugins.stream().filter(plugin -> "exec-maven-plugin".equals(plugin.getArtifactId()))
                .map(Plugin::getExecutions)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(execution -> execution.getGoals().contains("exec"))
                .filter(this::isHelmExec)
                .filter(this::containsOldSparkAppArg)
                .collect(Collectors.toList());
    }

    private boolean isHelmExec(PluginExecution execution) {
        Xpp3Dom executable = getConfig(execution, "executable");
        if (executable == null) {
            return false;
        }
        return "helm".equals(executable.getValue());
    }

    /**
     * Checks if the exec-maven-plugin execution contains the old aissemble-spark-application argument.
     *
     * @param execution the plugin <execution> to check
     * @return true if the execution contains the old argument
     */
    private boolean containsOldSparkAppArg(PluginExecution execution) {
        Xpp3Dom args = getConfig(execution, "arguments");
        if (args == null) {
            return false;
        }
        return Stream.of(args.getChildren("argument"))
                .anyMatch(arg -> "aissemble-spark-application".equals(arg.getValue()));
    }

    /**
     * Gets the <configuration> item for a given plugin <execution> by name.
     * @param execution the plugin <execution> to search
     * @param name the name of the configuration item to find
     * @return the configuration item DOM
     */
    private Xpp3Dom getConfig(PluginExecution execution, String name) {
        Xpp3Dom configuration = (Xpp3Dom) execution.getConfiguration();
        if (configuration == null) {
            return null;
        }
        return configuration.getChild(name);
    }
}
