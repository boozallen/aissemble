package com.boozallen.aissemble;

/*-
 * #%L
 * artifacts-plugin Maven Mojo
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Goal used to propagate all artifacts of a project to an alternate repository
 */
@Mojo(name = "propagate-all", requiresDependencyResolution = ResolutionScope.TEST, aggregator = true)
public class PropagateAllMojo extends MojoBase {

    @Parameter(property = "url", required = true)
    String url;

    @Parameter(property = "repositoryId", required = false, defaultValue = "")
    String repositoryId;
    
    @Override
    public void execute() throws MojoExecutionException {
        ArtifactsGoalHelper goalHelper = new ArtifactsGoalHelper(this);
        goalHelper.deployProjectDependencies(url, repositoryId);
    }

}
