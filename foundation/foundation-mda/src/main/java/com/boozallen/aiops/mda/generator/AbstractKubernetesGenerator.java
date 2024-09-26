package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.ManualActionNotificationService;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is responsible for generating Kubernetes resources within a Velocity context. It contains methods that retrieve application-related information from the context -
 * such as the application name, Aissemble version, and app dependencies. This class uses those retrieved values to configure the Velocity context and generate the Kubernetes resources.
 * 
 * This generator also interacts with the `ManualActionNotificationService` to handle 
 * application-specific notifications, such as Helm Tilt file messages and dependencies messages. 
 * 
 * The overall goal of this class is to automate the process of generating and configuring
 * Kubernetes resources within the given context of an application.
 */
public abstract class AbstractKubernetesGenerator extends AbstractResourcesGenerator {

    protected static final String APP_NAME = "appName";
    protected static final String APP_DEPENDENCIES = "appDependencies";
    protected static final String AISSEMBLE_VERSION = "aissembleVersion";
    protected static final String VERSION = "version";
    protected static final String DOCKER_PROJECT_REPOSITORY_URL = "dockerProjectRepositoryUrl";


    protected VelocityContext configureWithoutGeneration(GenerationContext generationContext) {
        final VelocityContext vc = getNewVelocityContext(generationContext);

        String appName = generationContext.getPropertyVariables().get(APP_NAME);
        String aissembleVersion = generationContext.getPropertyVariables().get(AISSEMBLE_VERSION);
        String dockerProjectRepositoryUrl = generationContext.getPropertyVariables().get(DOCKER_PROJECT_REPOSITORY_URL);

        if (StringUtils.isEmpty(appName)) {
            appName = generationContext.getArtifactId();
        }
        final String projectName = generationContext.getRootArtifactId();
        final String version = generationContext.getVersion();

        vc.put(APP_NAME, appName);
        vc.put(VelocityProperty.PROJECT_NAME, projectName);
        vc.put(VelocityProperty.VERSION_TAG, aissembleVersion);
        vc.put(VelocityProperty.VERSION, version);
        vc.put(VelocityProperty.DOCKER_PROJECT_REPOSITORY_URL, dockerProjectRepositoryUrl);

        // Set the application name for each individual module
        final String updatedOutputFile = replace(APP_NAME, generationContext.getOutputFile(), appName);
        generationContext.setOutputFile(updatedOutputFile);

        // Set the project version for each individual module
        final String updatedOutputFile2 = replace(VERSION, generationContext.getOutputFile(), version);
        generationContext.setOutputFile(updatedOutputFile2);

        final ManualActionNotificationService manualActionNotificationService = getNotificationService();
        final String deployArtifactId = generationContext.getArtifactId();
        if (!"configuration-store".equals(appName)) {
            addTiltNotification(generationContext, appName, deployArtifactId);
        }

        final String appDependencies = generationContext.getPropertyVariables().get(APP_DEPENDENCIES);
        if (!StringUtils.isEmpty(appDependencies)) {
            List<String> appDependencyList =
                Arrays.stream(appDependencies.split(",")).map(String::trim).collect(Collectors.toList());
            manualActionNotificationService.addResourceDependenciesTiltFileMessage(generationContext, appName, appDependencyList);
        }

        return vc;
    }

    protected ManualActionNotificationService getNotificationService() {
        return new ManualActionNotificationService();
    }

    protected void addTiltNotification(GenerationContext generationContext, String appName, String deployArtifactId) {
        getNotificationService().addHelmTiltFileMessage(generationContext, appName, deployArtifactId);
    }

    @Override
    public void generate(GenerationContext context) {
        VelocityContext vc = this.configureWithoutGeneration(context);
        generateFile(context, vc);
    }
}
