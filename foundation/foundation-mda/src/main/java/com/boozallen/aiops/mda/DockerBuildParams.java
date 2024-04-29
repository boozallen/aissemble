package com.boozallen.aiops.mda;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.technologybrewery.fermenter.mda.generator.GenerationContext;

public class DockerBuildParams {

    private GenerationContext context;
    private String appName;
    private String dockerApplicationArtifactId;
    private String dockerArtifactId;
    private String deployedAppName;
    private boolean includeHelmBuild;
    private boolean includeLatestTag;

    public DockerBuildParams(ParamBuilder builder) {
        context = builder.context;
        appName = builder.appName;
        dockerApplicationArtifactId = builder.dockerApplicationArtifactId;
        dockerArtifactId = builder.dockerArtifactId;
        deployedAppName = builder.deployedAppName;
        includeHelmBuild = builder.includeHelmBuild;
        includeLatestTag = builder.includeLatestTag;
    }

    public GenerationContext getContext() {
        return context;
    }

    public String getAppName() {
        return appName;
    }

    public String getDockerApplicationArtifactId() {
        return dockerApplicationArtifactId;
    }

    public String getDockerArtifactId() {
        return dockerArtifactId;
    }

    public String getDeployedAppName() {
        return deployedAppName;
    }

    public boolean isIncludeHelmBuild() {
        return includeHelmBuild;
    }

    public boolean isIncludeLatestTag() {
        return includeLatestTag;
    }

    public static final class ParamBuilder {

        private GenerationContext context;
        private String appName;
        private String dockerApplicationArtifactId;
        private String dockerArtifactId;
        private String deployedAppName;
        private boolean includeHelmBuild = false;
        private boolean includeLatestTag = false;

        public ParamBuilder() {
        }

        public ParamBuilder setContext(GenerationContext context) {
            this.context = context;
            return this;
        }

        public ParamBuilder setAppName(String appName) {
            this.appName = appName;
            return this;
        }

        public ParamBuilder setDockerApplicationArtifactId(String dockerApplicationArtifactId) {
            this.dockerApplicationArtifactId = dockerApplicationArtifactId;
            return this;
        }

        public ParamBuilder setDockerArtifactId(String dockerArtifactId) {
            this.dockerArtifactId = dockerArtifactId;
            return this;
        }

        public ParamBuilder setDeployedAppName(String deployedAppName) {
            this.deployedAppName = deployedAppName;
            return this;
        }

        public ParamBuilder setIncludeHelmBuild(boolean includeHelmBuild) {
            this.includeHelmBuild = includeHelmBuild;
            return this;
        }

        public ParamBuilder setIncludeLatestTag(boolean includeLatestTag) {
            this.includeLatestTag = includeLatestTag;
            return this;
        }

        public DockerBuildParams build() {
            return new DockerBuildParams(this);
        }
    }
}
