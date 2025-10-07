package com.boozallen.aiops.mda;

/*-
 * #%L
 * AIOps Foundation::aiSSEMBLE MDA
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

import com.boozallen.aiops.mda.enumeration.PipelineTypesEnum;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.technologybrewery.fermenter.mda.GenerateSourcesMojo;
import org.technologybrewery.fermenter.mda.element.ExpandedFamily;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mojo(name = "add")
public class GenerationMojo extends GenerateSourcesMojo implements org.apache.maven.plugin.Mojo {

    @Inject
    protected Prompter prompter;

    @Parameter(required = true, defaultValue = "test")
    protected String profile;

    @Parameter(required = true, defaultValue = "com.boozallen.aiops.mda")
    protected String basePackage;

    @Parameter(defaultValue = "${session}")
    private MavenSession session;

    @Parameter(defaultValue = "${maven.multiModuleProjectDirectory}")
    private String projectRoot;

    protected final String pipelineModelProfile = "pipeline-model";
    protected final String pipelineFamily = "pipeline";
    protected final String machineLearningImplementation = "machine-learning-mlflow";

    private HashMap<String, String> typeMap;

    protected ExpandedFamily selectedFamily;

    protected Map<String, String> properties;

    protected void setup() throws MojoExecutionException {
        typeMap = new HashMap<>();
        typeMap.put(PipelineTypesEnum.DATA_DELIVERY_SPARK_TYPE.getName(), PipelineTypesEnum.DATA_FLOW_IMPLEMENTATION.getName());
        typeMap.put(PipelineTypesEnum.DATA_DELIVERY_PYSPARK_TYPE.getName(), PipelineTypesEnum.DATA_FLOW_IMPLEMENTATION.getName());
        typeMap.put(PipelineTypesEnum.MACHINE_LEARNING_TYPE.getName(), PipelineTypesEnum.MACHINE_LEARNING_IMPLEMENTATION.getName());

        setTargetsFileLocation("targets.json");
        setProfilesFileLocation("profiles.json");

        properties = new HashMap<>();
        properties.put("baseDir", projectRoot);
        properties.put("baseArtifactName", projectRoot.substring(projectRoot.lastIndexOf('/')+1));

        setBasePackage(getProject().getGroupId());

        loadTargets();
        loadProfiles();
        loadFamilies();
    }

    @Override
    public void execute() throws MojoExecutionException {
        if(checkIfExecutionRoot()) {
            setup();

            try {
                String selectedFamilyName = promptFamily();
                promptProfile(selectedFamilyName);
                setPropertyVariables(properties);
                super.execute();

            } catch (PrompterException e) {
                throw new MojoExecutionException("Failed to add new module to the project!", e);
            }
        }
    }

    protected String promptFamily() throws PrompterException {
        Map<String, ExpandedFamily> families = getFamilies();
        String familyNames = String.join(", ", families.keySet());

        String selectedFamilyName =
                prompter.prompt("What would you like to add? [Options: " + familyNames + "]");
        selectedFamily = families.get(selectedFamilyName);

        if (pipelineFamily.equals(selectedFamilyName)) {
            // this is confusing because profile is an overloaded term. this sets the maven profile,
            // not the fermenter profile. currently pipeline is the only option, need to update as we add more
            setProfile(pipelineModelProfile);
            promptPipelineName();
        }

        return selectedFamilyName;
    }

    protected void promptProfile(String selectedFamilyName) throws PrompterException {
        List<String> profiles = selectedFamily.getProfiles().stream().map(profile ->
                PipelineTypesEnum.getByName(profile.getName()).getOrder() + " (" + profile.getName() + ")"
        ).collect(Collectors.toList());
        String profileNames = String.join(", ", profiles);

        String selectedProfileName = null;
        while(selectedProfileName == null) {
            String input = prompter.prompt("Enter the desired " + selectedFamilyName +
                    " implementation. [Options: " + profileNames + "]");
            int selectedProfileNum = Integer.parseInt(input);
            PipelineTypesEnum profileEnum = PipelineTypesEnum.getByOrder(selectedProfileNum);

            if(profileEnum == null) {
                getLog().error("Please select an option from the list.");
            } else {
                selectedProfileName =  profileEnum.getName();
            }
        }

        // machine learning pipelines are handled a bit differently because they generate a training and inference
        // pipeline. because of this, the profile reference doesn't match the implementation type name
        if(PipelineTypesEnum.MACHINE_LEARNING_TYPE.toString().equals(selectedProfileName)) {
            properties.put("implementationName", machineLearningImplementation);
        } else {
            properties.put("implementationName", selectedProfileName);
        }

        properties.put("pipelineTypeName", typeMap.get(selectedProfileName));
    }

    protected void promptPipelineName() throws PrompterException {
        String name = prompter.prompt("What would you like to name your pipeline?");
        name = Character.toUpperCase(name.charAt(0))  + name.substring(1);
        properties.put("pipelineName", name);
    }

    protected void generateSources() throws MojoExecutionException {
        super.execute();
    }

    protected boolean checkIfExecutionRoot() {
        String directory = getProject().getModel().getProjectDirectory().toString();
        return session.getExecutionRootDirectory().equalsIgnoreCase(directory);
    }
}
