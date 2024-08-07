package com.boozallen.aiops.mda;

/*-
 * #%L
 * AIOps Foundation::aiSSEMBLE MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.components.interactivity.PrompterException;

@Mojo(name = "add-pipeline")
public class AddPipelineMojo extends GenerationMojo implements org.apache.maven.plugin.Mojo {

    @Override
    public void execute() throws MojoExecutionException {
        if(checkIfExecutionRoot()) {
            setup();
            setProfile(pipelineModelProfile);
            selectedFamily = getFamilies().get(pipelineFamily);

            try {
                promptPipelineName();
                promptProfile(pipelineFamily);
            } catch (PrompterException e) {
                throw new MojoExecutionException("Failed to add new module to the project!", e);
            }

            setPropertyVariables(properties);

            super.generateSources();
        }
    }
}
