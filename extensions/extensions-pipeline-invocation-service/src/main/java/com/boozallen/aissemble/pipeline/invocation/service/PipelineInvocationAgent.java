package com.boozallen.aissemble.pipeline.invocation.service;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Pipeline Invocation Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.pipeline.invocation.service.serialization.PipelineInvocationRequest;
import com.boozallen.aissemble.pipeline.invocation.service.util.exec.HelmCommandExecutor;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Intermediary layer between user-facing endpoints and cluster operations.  Manages fault-tolerance, input manipulation,
 * and any other non-cluster operations common to all endpoints.
 * TODO: Authorization checks
 */
@ApplicationScoped
public class PipelineInvocationAgent {
    public enum FailureStrategy {
        EXCEPTIONAL,LOG,SILENT
    }

    @Inject
    private HelmCommandExecutor executor;

    @Inject
    private ValuesFileRegistry registry;

    private static final Logger logger = Logger.getLogger(PipelineInvocationAgent.class);
    @ConfigProperty(name = "service.pipelineInvocation.failureStrategy.global", defaultValue = "LOG")
    private FailureStrategy globalFailureStrategy;

    /**
     * Allows overriding the global failure strategy at a per-pipeline level
     * @param applicationName Application name to apply the new strategy to
     */
    public FailureStrategy getPipelineFailureStrategy(String applicationName) {
        String propertyName = String.format("service.pipelineInvocation.failureStrategy.%s", applicationName);
        return ConfigProvider.getConfig().getOptionalValue(propertyName, FailureStrategy.class).orElse(globalFailureStrategy);
    }

    private void handleFailure(Exception ex, String applicationName) {
        FailureStrategy failureStrategy = getPipelineFailureStrategy(applicationName);
        logger.debug("Proceeding with failure strategy " + failureStrategy.name() + ".");
        switch(failureStrategy) {
            case EXCEPTIONAL: throw new RuntimeException(ex);
            case LOG:
                logger.error("Error encountered submitting application " + applicationName + " to the cluster: ", ex);
                break;
            case SILENT: break;
        }
    }

    /**
     * Entrypoint triggering the user-specified SparkApplication to be submitted to the cluster, with all associated
     * handling and overhead processing.
     * @param pipelineInvocationRequest Deserialized object containing provided parameters.
     */
    public void submitSparkApplication(PipelineInvocationRequest pipelineInvocationRequest) {
        executor.uninstallReleaseIfPresent(pipelineInvocationRequest.getApplicationName());

        try {
            executor.executeAndLogOutput(buildHelmInstallCommandArgs(pipelineInvocationRequest));
        } catch(Exception exception) {
            this.handleFailure(exception, pipelineInvocationRequest.getApplicationName());
        }
    }

    /**
     * Helper function to build the composite helm command
     * @param pipelineInvocationRequest Deserialized request
     * @return List representing helm install command args
     */
    public List<String> buildHelmInstallCommandArgs(PipelineInvocationRequest pipelineInvocationRequest) {
        List<String> args = executor.createBaseHelmInstallArgs(pipelineInvocationRequest.getApplicationName());
        args.addAll(buildHelmValuesArgs(pipelineInvocationRequest));
        args.addAll(buildFinalValuesOverrides(pipelineInvocationRequest));

        return args;
    }

    /**
     * Translates values overrides from the initial request to the appropriate helm command args
     * @param request Deserialized object containing provided parameters.
     * @return List of appropriate command arguments.
     */
    protected List<String> buildFinalValuesOverrides(PipelineInvocationRequest request) {
        List<String> args = new ArrayList<>();

        args.add("--set");
        args.add("spec.serviceEnabled=false");

        for(Map.Entry<String, String> entry : request.getOverrideValues().entrySet()) {
            args.add("--set");
            args.add(entry.getKey() + "=" + entry.getValue());
        }

        return args;
    }

    /**
     * Translates an execution profile from the initial request to the appropriate helm command args
     * @param request Deserialized object containing provided parameters.
     * @return List of appropriate command arguments.
     */
    protected List<String> buildHelmValuesArgs(PipelineInvocationRequest request) {
        List<String> args = new ArrayList<>();

        for(String classifier : request.getProfile().getLayers()) {
            args.add("--values");
            args.add(registry.getValuesCollection(request.getApplicationName())
                    .getPathForClassifier(classifier)
                    .toAbsolutePath()
                    .toString()
            );
        }

        return args;
    }
}
