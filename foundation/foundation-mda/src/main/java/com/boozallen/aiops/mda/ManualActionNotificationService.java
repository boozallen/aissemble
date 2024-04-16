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

import com.boozallen.aiops.mda.generator.util.MavenUtil;
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.generator.GenerationException;
import org.technologybrewery.fermenter.mda.notification.Notification;
import org.technologybrewery.fermenter.mda.notification.NotificationCollector;
import org.technologybrewery.fermenter.mda.notification.VelocityNotification;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.inject.Named;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class to aid in notifying users of required actions.
 */
@Named
public class ManualActionNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(ManualActionNotificationService.class);
    private static final String EMPTY_LINE = "\n";
    private static final String SUPPRESS_WARNINGS = "maven-suppress-warnings";
    private static final String SUPPRESS_TILT_WARNINGS_MSG = "* NOTE: If you do not want to see this message add '# " + SUPPRESS_WARNINGS + "' to the file\n";
    public static final String GROUP_TILT = "tilt";

    public void addSchemaElementDeprecationNotice(String illegalElement, String objectType) {
        final String SCHEMA_ELEMENT_DEPRECATION_KEY = "schema_element_deprecation";

        VelocityNotification notification = new VelocityNotification(SCHEMA_ELEMENT_DEPRECATION_KEY, "schemaelementdeprecation", new HashSet<String>(), "templates/notifications/notification.schema.element.deprecation.vm");
        notification.addToVelocityContext("objectType", objectType);
        notification.addToVelocityContext("illegalElement", illegalElement);
        addManualAction(SCHEMA_ELEMENT_DEPRECATION_KEY, notification);
    }

    /**
     * Adds message to build output indicating that no pipelines have been defined but are required.
     *
     * @param context contextual information about what is being generated
     */
    public void addNoticeToAddPipelines(GenerationContext context) {

        final String ADD_PIPELINES_KEY = "add_pipelines";

        final String pathToReadMe = context.getProjectDirectory().toString()
                .replace("pipelines", "pipeline-models/pipelines/README.md");

        VelocityNotification notification = new VelocityNotification(ADD_PIPELINES_KEY, new HashSet<>(), "templates/notifications/notification.pipelines.vm");
        notification.addToVelocityContext("pathToReadMe", pathToReadMe);
        addManualAction(ADD_PIPELINES_KEY, notification);
    }

    /**
     * Checks if updates to the modules for a POM file are necessary and adds a notification if so.
     * Using submoduleDepth is a placeholder solution that will be replaced with a more long-term solution (such as updating Fermenter).
     *
     * @param context    the generation context
     * @param artifactId the artifact ID
     * @param moduleType the module type
     */
    public void addNoticeToAddModuleToParentBuild(GenerationContext context, String artifactId, String moduleType) {

        final String parentArtifactId = context.getArtifactId();
        final String pomFilePath = context.getProjectDirectory() + File.separator + "pom.xml";
        final String query = "<module>" + artifactId + "</module>";
        boolean alreadyExists = existsInFile(pomFilePath, query);

        File projectDirectory = context.getProjectDirectory();
        File executionRootDirectory = context.getExecutionRootDirectory();
        StringBuilder parentDirectory = new StringBuilder();

        while (!executionRootDirectory.equals(projectDirectory)) {

            parentDirectory.insert(0, projectDirectory.getParentFile().getName() + File.separator);
            projectDirectory = projectDirectory.getParentFile();
        }

        String displayPomFilePath = parentDirectory + parentArtifactId + File.separator + "pom.xml";

        if (!alreadyExists) {
            final String key = getMessageKey(pomFilePath, "module");

            HashSet<String> items = new HashSet<String>();
            items.add("<module>" + (artifactId) + "</module>");

            VelocityNotification notification = new VelocityNotification(key, items, "templates/notifications/notification.module.to.parent.vm");
            notification.addToVelocityContext("moduleType", moduleType);
            notification.addToVelocityContext("displayPomFilePath", displayPomFilePath);
            notification.addToVelocityContext("artifactId", artifactId);
            addManualAction(pomFilePath, notification);
        }

    }

    /**
     * Checks if updates are needed to dependencies in a POM file.
     *
     * @param context     the generation context
     * @param artifactId  the artifact ID
     * @param persistType the persist type
     */
    public void addNoticeToAddDependency(GenerationContext context, String artifactId, String persistType) {

        final String pomFilePath = context.getProjectDirectory() + File.separator + artifactId + File.separator + "pom.xml";
        boolean alreadyExists = existsInFile(pomFilePath, String.format("extensions-data-delivery-spark-%s", persistType));

        if (!alreadyExists) {
            final String key = getMessageKey(pomFilePath, "extensions-data-delivery-spark");

            VelocityNotification notification = new VelocityNotification(key, new HashSet<>(), "templates/notifications/notification.dependency.vm");
            notification.addToVelocityContext("dependencyArtifactId", String.format("extensions-data-delivery-spark-%s", persistType));
            notification.addToVelocityContext("artifactId", artifactId);
            addManualAction(pomFilePath, notification);
        }
    }

    /**
     * Checks if there are deployment changes necessary for the live update feature in
     * Tiltfile and adds a message if so.
     *
     * @param context                     the generation context
     * @param dockerArtifactId            the docker artifact ID
     * @param dockerApplicationArtifactId the docker application artifact ID
     * @param pipelineName                the pipeline's name
     * @param stepName                    the pipeline step's name
     * @param includeHelmBuild whether to include the helm build in the message
     */
    public void addDockerBuildWithLiveUpdateTiltFileMessage(final GenerationContext context, final String dockerArtifactId, final String dockerApplicationArtifactId, 
                                                            final String pipelineName, final String stepName, final boolean includeHelmBuild) {
        final File rootDir = context.getExecutionRootDirectory();
        if (!rootDir.exists() || !tiltFileFound(rootDir)) {
            logger.warn("Unable to find Tiltfile. Will not be able to direct manual Dockerbuild updates to Tiltfile");
        } else {
            final String tiltFilePath = rootDir.getAbsolutePath() + File.separator + "Tiltfile";
            final String referenceName = "boozallen/" + dockerApplicationArtifactId;
            final String pipelineArtifactId = dockerArtifactId.replace("-docker", "-pipelines");
            final String compileText = "compile-" + dockerApplicationArtifactId;
            final String stepNameSnakeCase = PipelineUtils.deriveLowerSnakeCaseNameFromHyphenatedString(stepName);
            boolean tiltFileContainsArtifact = existsInFile(tiltFilePath, referenceName);
            if (!tiltFileContainsArtifact && showWarnings(tiltFilePath)) {
                final String key = getMessageKey(tiltFilePath, "Tiltfile");

                addLocalResourceTiltFileMessage(context, dockerArtifactId, dockerApplicationArtifactId, stepName, pipelineName + "/" + stepName, true);

                VelocityNotification notification = new VelocityNotification(key, new HashSet<>(), "templates/notifications/notification.docker.live.vm");
                notification.addToVelocityContext("dockerApplicationArtifactId", dockerApplicationArtifactId);
                notification.addToVelocityContext("stepName", stepName);
                notification.addToVelocityContext("pipelineArtifactId", pipelineArtifactId);
                notification.addToVelocityContext("pipelineName", pipelineName);
                notification.addToVelocityContext("dockerArtifactId", dockerArtifactId);
                notification.addToVelocityContext("referenceName", referenceName);
                notification.addToVelocityContext("stepNameSnakeCase", stepNameSnakeCase);
                notification.addToVelocityContext("includeHelmBuild", includeHelmBuild);
                addManualAction(tiltFilePath, notification);
            }
        }
    }

    /**
     * Checks if there are deployment changes necessary in either the Maven POM file or Tiltfile and
     * adds a message if so.
     *
     * @param context                     the generation context
     * @param appName                     the application name
     * @param dockerApplicationArtifactId the docker application artifact ID
     * @param dockerArtifactId            the docker artifact ID
     * @param dockerImage                 use custom image instead of dockerApplicationArtifactId
     * @param deployedAppName             the application name after deployment
     * @param includeHelmBuild            whether to include the helm build in the message
     * @param includeLatestTag            whether to include the latest tag as a tilt extra tag
     */
    public void addDockerBuildTiltFileMessage(DockerBuildParams params) {

        final File rootDir = params.getContext().getExecutionRootDirectory();

        if (!rootDir.exists() || !tiltFileFound(rootDir)) {
            logger.warn("Unable to find Tiltfile. Will not be able to direct manual Dockerbuild updates to Tiltfile");
        } else {
            final String tiltFilePath = rootDir.getAbsolutePath() + File.separator + "Tiltfile";
            final String text = "boozallen/" + params.getDockerApplicationArtifactId();
            final String deployArtifactId = params.getDockerArtifactId().replace("-docker", "-deploy");
            boolean tiltFileContainsArtifact = existsInFile(tiltFilePath, text);

            if (!tiltFileContainsArtifact && showWarnings(tiltFilePath)) {
                VelocityNotification notification = new VelocityNotification("docker-build-" + params.getDeployedAppName(),
                        GROUP_TILT, new HashSet<>(), "templates/notifications/notification.docker.tilt.vm");
                notification.addToVelocityContext("appNameTitle", params.getAppName());
                notification.addToVelocityContext("dockerArtifactId", params.getDockerArtifactId());
                notification.addToVelocityContext("dockerApplicationArtifactId", params.getDockerApplicationArtifactId());
                notification.addToVelocityContext("includeHelmBuild", params.isIncludeHelmBuild());
                notification.addToVelocityContext("appName", params.getDeployedAppName());
                notification.addToVelocityContext("deployArtifactId", deployArtifactId);
                notification.addToVelocityContext("includeLatestTag", params.isIncludeLatestTag());
                addManualAction(tiltFilePath, notification);
            }
        }
    }

    /**
     * Notification to add the function which sets the habushu dist artifact version property in the root pom for
     * access across modules.
     *
     * @param context generation context
     */
    public void addHabushuRegexPluginInvocation(final GenerationContext context) {
        if(!existsInFile("pom.xml", "<id>set-habushu-dist-artifact-version</id>")) {
            VelocityNotification notification = new VelocityNotification(
                    "<id>set-habushu-dist-artifact-version</id>",
                    "root-plugins",
                    new HashSet<>(),
                    "templates/notifications/notification.root.habushu.regex.plugin.vm"
            );
            addManualAction(context.getRootArtifactId(), notification);
        }
    }

    /**
     * Adds the maven-clean-plugin to the deploy pom.  Needed in order to reset the content of app target directories.
     *
     * @param deployArtifactId Deploy module artifact ID
     * @param context generation context
     */
    public void addCleanPluginNotification(final String deployArtifactId, final GenerationContext context) {
        if(deployArtifactId != null && !existsInFile(deployArtifactId + "/pom.xml", "<artifactId>maven-clean-plugin</artifactId>")) {
            VelocityNotification notification = new VelocityNotification(
                    "clean-deploy-apps-targets",
                    "deploy-plugins",
                    new HashSet<>(),
                    "templates/notifications/notification.deploy.clean.app.target.vm"
            );
            addManualAction(context.getRootArtifactId() + "/" + deployArtifactId, notification);
        }
    }

    /**
     * Manual action for adding the pipeline invocation service deployment execution and all associated plugin
     * executions.
     * @param context generation context
     */
    public void addPipelineInvocationServiceDeployment(final GenerationContext context) {
        String deployArtifactId = MavenUtil.getDeployModuleName(context.getExecutionRootDirectory());
        if(deployArtifactId != null && !existsInFile(deployArtifactId + "/pom.xml", "<artifactId>mda-maven-plugin</artifactId>")) {
            VelocityNotification notification = new VelocityNotification(
                    "pipeline-invocation-service-spark-apps",
                    "deploy-plugins",
                    new HashSet<>(),
                    "templates/notifications/notification.mda.maven.pipeline.invocation.execution.vm"
            );
            notification.addToExternalVelocityContextProperties("deployArtifactId", deployArtifactId);
            addManualAction(context.getRootArtifactId() + "/" + deployArtifactId, notification);
            addDeployPomMessage(context, "pipeline-invocation-service-v2", "pipeline-invocation-service");
        }
        addCleanPluginNotification(deployArtifactId, context);
        addHabushuRegexPluginInvocation(context);
    }

    // TODO: Standardize the spark, inference, and training docker folders and dockerfile to use the same structure so
    // that live updates can be applied with the same pattern copyFullPath variable should be removed after the
    // structure for target folder is established.
    /**
     * Checks if there are deployment changes necessary for live updating a specific python step module's source code in
     * Tiltfile and adds a message if not.
     *
     * @param context                     the generation context
     * @param dockerArtifactId            the project's docker module
     * @param dockerApplicationArtifactId the docker image module that should be live-updated with changes
     * @param moduleName                  the module name to compile for live updates
     * @param pipelinesModulePath         the path to the module under the project's "pipelines" module (e.g. "ml-pipeline/training-step")
     * @param copyFullPath                this variable is temporary and should be removed (see TODO); determines whether to copy the generated output to a different folder path
     */
    public void addLocalResourceTiltFileMessage(final GenerationContext context, final String dockerArtifactId,
                                                final String dockerApplicationArtifactId, final String moduleName,
                                                final String pipelinesModulePath, final boolean copyFullPath) {
        final File rootDir = context.getExecutionRootDirectory();
        if (!rootDir.exists() || !tiltFileFound(rootDir)) {
            logger.warn("Unable to find Tiltfile. Will not be able to direct manual Dockerbuild updates to Tiltfile");
        } else {
            final String tiltFilePath = rootDir.getAbsolutePath() + File.separator + "Tiltfile";
            final String pipelinesArtifactId = dockerArtifactId.replace("-docker", "-pipelines");
            boolean tiltFileContainsArtifact = existsInFile(tiltFilePath, "compile-" + moduleName);
            if (!tiltFileContainsArtifact && showWarnings(tiltFilePath)) {
                final String key = getMessageKey(tiltFilePath, "Tiltfile");

                VelocityNotification notification = new VelocityNotification(key, new HashSet<>(), "templates/notifications/notification.local.resource.tilt.vm");
                notification.addToVelocityContext("srcModuleName", moduleName);
                notification.addToVelocityContext("srcModulePath", pipelinesArtifactId + "/" + pipelinesModulePath);
                notification.addToVelocityContext("dockerModulePath", dockerArtifactId + "/" + dockerApplicationArtifactId);
                notification.addToVelocityContext("copyFullPath", copyFullPath);
                addManualAction(tiltFilePath, notification);
            }
        }
    }
    
    /**
     * Checks if there are deployment changes necessary for PySpark live update feature in
     * Tiltfile and adds a message if so.
     *
     * @param context                     the generation context
     * @param appName                     the app name
     * @param dockerApplicationArtifactId the docker application artifact ID
     * @param dockerArtifactId            the docker artifact ID
     */
    public void addDockerBuildTiltFileMessage(final GenerationContext context, final String appName, final String dockerApplicationArtifactId, final String dockerArtifactId) {
        addDockerBuildTiltFileMessage(context, appName, dockerApplicationArtifactId, dockerArtifactId);
    }

    /**
     * Checks if there are deployment changes necessary in the Tiltfile for Spark Worker and
     * adds a message if so.
     *
     * @param context                     the generation context
     * @param appName                     the application name
     * @param dockerApplicationArtifactId the docker application artifact ID
     * @param dockerArtifactId            the docker artifact ID
     * @param includeHelmBuild            whether to include the helm build in the message
     * @param includeLatestTag            whether to include the latest tag as a tilt extra tag
     */
    public void addSparkWorkerDockerBuildTiltMessage(final GenerationContext context, final String appName, final String dockerApplicationArtifactId,
                                                     final String dockerArtifactId) {

        final File rootDir = context.getExecutionRootDirectory();
        if (!rootDir.exists() || !tiltFileFound(rootDir)) {
            logger.warn("Unable to find Tiltfile. Will not be able to direct manual Spark Worker resources to Tiltfile");
        } else {
            //creates the instructions for the spark worker image
            DockerBuildParams params = new DockerBuildParams.ParamBuilder().setContext(context)
                    .setAppName(appName)
                    .setDockerApplicationArtifactId(dockerApplicationArtifactId)
                    .setDockerArtifactId(dockerArtifactId)
                    .setIncludeHelmBuild(false)
                    .setIncludeLatestTag(true)
                    .build();
            addDockerBuildTiltFileMessage(params);

            final String tiltFilePath = rootDir.getAbsolutePath() + File.separator + "Tiltfile";
            final String text = "k8s_kind('SparkApplication'";

            boolean tiltFileContainsArtifact = existsInFile(tiltFilePath, text);
            if (!tiltFileContainsArtifact && showWarnings(tiltFilePath)) {
                VelocityNotification notification = new VelocityNotification("spark-worker", GROUP_TILT, new HashSet<>(),
                        "templates/notifications/notification.spark.worker.docker.build.tilt.vm");
                addManualAction(tiltFilePath, notification);
            }
        }
    }

    /**
     * Adds a notification to update the Tiltfile.
     *
     * @param context          the generation context
     * @param appName          the application name
     * @param deployArtifactId the deploy artifact ID
     */
    public void addHelmTiltFileMessage(final GenerationContext context, final String appName,
                                       final String deployArtifactId) {

        final File rootDir = context.getExecutionRootDirectory();
        if (!rootDir.exists() || !tiltFileFound(rootDir)) {
            logger.warn("Unable to find Tiltfile. Will not be able to direct manual Helm updates to for Tiltfile");
        } else {
            final String tiltFilePath = rootDir.getAbsolutePath() + File.separator + "Tiltfile";
            final String text = "apps/" + appName + "'";

            boolean tiltFileContainsArtifact = existsInFile(tiltFilePath, text);
            if (!tiltFileContainsArtifact && showWarnings(tiltFilePath)) {
                VelocityNotification notification = new VelocityNotification("helm-" + appName, GROUP_TILT, new HashSet<String>(), "templates/notifications/notification.helm.tilt.vm");
                notification.addToVelocityContext("appName", appName);
                notification.addToVelocityContext("deployArtifactId", deployArtifactId);
                notification.addToVelocityContext("projectName", context.getRootArtifactId());
                addManualAction(tiltFilePath, notification);
            }

        }
    }

    /**
     * Adds a notification to update the Tiltfile for resources dependent on another resource
     *
     * @param context         the generation context
     * @param appName         the application name
     * @param appDependencies application names this resource is dependent on
     */
    public void addResourceDependenciesTiltFileMessage(GenerationContext context, String appName, List<String> appDependencies) {
        final File rootDir = context.getExecutionRootDirectory();
        if (!rootDir.exists() || !tiltFileFound(rootDir)) {
            logger.warn("Unable to find Tiltfile. Will not be able to direct manual resource dependency updates to for Tiltfile");
        } else {
            final String tiltFilePath = rootDir.getAbsolutePath() + File.separator + "Tiltfile";

            final StringBuilder item = new StringBuilder();
            final String formattedAppDependencies = appDependencies.stream().map(s -> "'" + s + "'").collect(Collectors.joining(","));
            item.append(String.format("k8s_resource('%s', resource_deps=[%s])\n\n", appName, formattedAppDependencies));

            boolean tiltFileContainsArtifact = existsInFile(tiltFilePath, item.toString().trim());
            if (!tiltFileContainsArtifact && showWarnings(tiltFilePath)) {
                VelocityNotification notification = new VelocityNotification("resource-dependencies", new HashSet<>(), "templates/notifications/notification.resource.tilt.vm");
                notification.addToVelocityContext("appName", appName);
                notification.addToVelocityContext("formattedAppDependencies", formattedAppDependencies);
                addManualAction(tiltFilePath, notification);
            }
        }
    }

    private void appendTiltHelmBuild(String appName, String deployArtifactId, StringBuilder builder) {
        builder.append("yaml = helm(\n");
        builder.append("   '")
                .append(deployArtifactId)
                .append("/src/main/resources/apps/")
                .append(appName)
                .append("',\n");
        builder.append("   values=['")
                .append(deployArtifactId)
                .append("/src/main/resources/apps/")
                .append(appName)
                .append("/values.yaml',\n");
        builder.append("       '")
                .append(deployArtifactId)
                .append("/src/main/resources/apps/")
                .append(appName)
                .append("/values-dev.yaml']\n");
        builder.append(")\n");
        builder.append("k8s_yaml(yaml)\n");
        builder.append(EMPTY_LINE);
    }

    /**
     * Adds a notification to update the Tiltfile.
     * NOTE: This "k8s_yaml" line is also being output by appendTiltHelmBuild() and addSparkWorkerTiltResources() so
     * we should consider refactoring to extract out common code.
     *
     * @param context          the generation context
     * @param appName          the application name
     * @param deployArtifactId the deploy artifact ID
     * @param yamlFileName     the deploy artifact ID
     */
    public void addYamlTiltFileMessage(final GenerationContext context, final String appName,
                                       final String deployArtifactId, final String yamlFileName) {

        final File rootDir = context.getExecutionRootDirectory();
        if (!rootDir.exists() || !tiltFileFound(rootDir)) {
            logger.warn("Unable to find Tiltfile. Will not be able to direct manual Helm updates to for Tiltfile");
        } else {
            final String tiltFilePath = rootDir.getAbsolutePath() + File.separator + "Tiltfile";
            final String yamlFilePath = deployArtifactId + "/src/main/resources/apps/" + appName + "/" + yamlFileName;
            final String text = "apps/" + appName;

            boolean tiltFileContainsArtifact = existsInFile(tiltFilePath, text);
            if (!tiltFileContainsArtifact && showWarnings(tiltFilePath)) {
                final String key = getMessageKey(tiltFilePath, "Tiltfile");

                HashSet<String> items = new HashSet<String>();
                items.add(yamlFilePath);

                VelocityNotification notification = new VelocityNotification("yaml", GROUP_TILT, items,
                        "templates/notifications/notification.yaml.tiltfile.vm");
                addManualAction(tiltFilePath, notification);
            }
        }
    }

    /**
     * Adds a notification to update the tiltfile with necessary elasticsearch resources
     *
     * @param context    the generation context
     * @param appName    the application name
     * @param artifactId the artifact ID
     */
    public void addElasticsearchTiltResources(final GenerationContext context, final String appName, final String artifactId) {
        final File rootDir = context.getExecutionRootDirectory();
        if (!rootDir.exists() || !tiltFileFound(rootDir)) {
            logger.warn("Unable to find Tiltfile. Will not be able to direct manual Helm updates to for Tiltfile");
        } else {
            final String tiltFilePath = rootDir.getAbsolutePath() + File.separator + "Tiltfile";
            final String[] elasticSearchResources = {"k8s_kind('Elasticsearch')", "k8s_resource('elasticsearch')"};

            HashSet<String> items = new HashSet<String>();

            //loop through each resource to see if it's in the tiltfile
            for (String resource : elasticSearchResources) {
                boolean tiltFileContainsArtifact = existsInFile(tiltFilePath, resource);
                if (!tiltFileContainsArtifact && showWarnings(tiltFilePath)) {
                    items.add(resource);
                }
            }

            //add a manual action for any resources that may need to be added
            if (items.size() > 0) {
                VelocityNotification notification = new VelocityNotification("elasticsearch", GROUP_TILT, items,
                        "templates/notifications/notification.elastic.search.tilt.vm");
                addManualAction(tiltFilePath, notification);
            }
        }
    }


    /**
     * Adds a notification to update the tiltfile with necessary spark worker resources
     * NOTE: Consider refactor to leverage addYamlTiltFileMessage() for "k8s_yaml" line
     *
     * @param context                the generation context
     * @param parentArtifactId       the name of the parent directory the pipelines are in
     * @param pipelineArtifactId     the artifact id of the pipeline
     * @param pipelineImplementation the implementation of the pipeline
     */
    public void addSparkWorkerTiltResources(final GenerationContext context, final String parentArtifactId,
                                            final String pipelineArtifactId, final String pipelineImplementation) {

        final File rootDir = context.getExecutionRootDirectory();
        if (!rootDir.exists() || !tiltFileFound(rootDir)) {
            logger.warn("Unable to find Tiltfile. Will not be able to direct manual Spark Worker resources to Tiltfile");
        } else {
            final String tiltFilePath = rootDir.getAbsolutePath() + File.separator + "Tiltfile";
            final String text = "--values " + parentArtifactId + "/" + pipelineArtifactId;

            boolean tiltFileContainsArtifact = existsInFile(tiltFilePath, text);
            if (!tiltFileContainsArtifact && showWarnings(tiltFilePath)) {
                VelocityNotification notification = new VelocityNotification("spark-worker", GROUP_TILT, new HashSet<>(),
                        "templates/notifications/notification.spark.worker.tilt.vm");
                notification.addToVelocityContext("parentArtifactId", parentArtifactId);
                notification.addToVelocityContext("pipelineArtifactId", pipelineArtifactId);
                notification.addToVelocityContext("pipelineImplementation", pipelineImplementation);
                notification.addToVelocityContext("pythonPipelineArtifactId", PipelineUtils.deriveLowerSnakeCaseNameFromHyphenatedString(pipelineArtifactId));
                notification.addToVelocityContext("aissembleVersion", PipelineUtils.getAiSSEMBLEVersion(context));
                addManualAction(tiltFilePath, notification);
            }
        }
    }

    /**
     * Adds a notification to update the pom.xml for the deploy module.
     *
     * @param context the generation context
     * @param profile the profile to add
     * @param appName the application name to add
     */
    public void addDeployPomMessage(final GenerationContext context, final String profile, final String appName) {
        addDeployPomMessage(context, profile, appName, null);
    }

    /**
     * Adds a notification to update the pom.xml for the docker module.
     *
     * @param context         the generation context
     * @param profile         the profile to add
     * @param appName         the application name to add
     * @param appDependencies application names this resource is dependent on
     */
    public void addDeployPomMessage(final GenerationContext context, final String profile, final String appName, List<String> appDependencies) {
        final File rootDir = context.getExecutionRootDirectory();
        if (!rootDir.exists() || !deployModuleFoundPom(rootDir)) {
            logger.warn("Unable to find Docker module. Will not be able to direct manual updates for the deploy module's POM.xml");
        } else {
            if (StringUtils.isNotEmpty(profile)) {
                NotificationParams params = configureNotification(rootDir, profile, appName, MavenUtil::getDeployModuleName);
                boolean hasAppDependencies = appDependencies != null;

                if (!params.isExistsInFileOrNotification()) {

                    VelocityNotification notification = new VelocityNotification(params.getKey(), "deploypom", new HashSet<>(), "templates/notifications/notification.deploy.pom.vm");
                    notification.addToVelocityContext("appName", appName);
                    notification.addToVelocityContext(("profile"), profile);
                    notification.addToVelocityContext("basePackage", context.getBasePackage());
                    notification.addToVelocityContext("profileConfiguration", params.getProfileConfiguration());
                    notification.addToVelocityContext("hasAppDependencies", hasAppDependencies);
                    notification.addToVelocityContext("appDependencies", appDependencies);

                    notification.addToExternalVelocityContextProperties("deployArtifactId", params.getArtifactId());

                    addManualAction(params.getPomFilePath(), notification);
                } else if (hasAppDependencies && !propertyVariableExistsInPomFile(params.getPomFile(), appName, "appDependencies", String.join(",", appDependencies))) {

                    params.setKey(getMessageKey(params.getPomFilePath(), "execution", "appDependencies", appName));
                    VelocityNotification notification = new VelocityNotification(params.getKey(), new HashSet<String>(), "templates/notifications/notification.deploy.pom.property.variables.vm");
                    notification.addToVelocityContext("profile", profile);
                    notification.addToVelocityContext("deployArtifactId", params.getArtifactId());
                    notification.addToVelocityContext("appDependencies", appDependencies);
                    addManualAction(params.getPomFilePath(), notification);
                }
            }
        }
    }

    /**
     * Adds a notification to update the pom.xml for the docker module.
     *
     * @param context the generation context
     * @param profile the profile to add
     */
    public void addDockerPomMessage(final GenerationContext context, final String profile, final String artifactId) {
        final File optionalRoot = context.getExecutionRootDirectory();
        if (!optionalRoot.exists() || !dockerModuleFoundPom(optionalRoot)) {
            logger.warn("Unable to find Docker module. Will not be able to direct manual updates for the deploy module's POM.xml");
        } else {
            NotificationParams params = configureNotification(optionalRoot, profile, artifactId, MavenUtil::getDockerModuleName);
            if (StringUtils.isNotEmpty(profile)) {
                if (!params.isExistsInFileOrNotification()) {

                    VelocityNotification notification = new VelocityNotification(params.getKey(), "dockerpom", new HashSet<>(), "templates/notifications/notification.docker.pom.vm");
                    notification.addToVelocityContext(("profile"), profile);
                    notification.addToVelocityContext("appName", artifactId);
                    notification.addToVelocityContext("basePackage", context.getBasePackage());
                    notification.addToVelocityContext("profileConfiguration", params.getProfileConfiguration());
                    notification.addToExternalVelocityContextProperties("dockerArtifactId", params.getArtifactId());

                    addManualAction(params.getPomFilePath(), notification);
                }
            }
        }
    }
    /**
     * Adds a notification to inform user to change the pom.xml for the sagemaker docker module.
     *
     * @param context                  the generation context
     * @param artifactId               the artifactId
     * @param trainingDockerArtifactId the pom file in dockerArifactId 
     */
    public void addSagemakerDockerPomMessage(final GenerationContext context, final String artifactId, final String trainingDockerArtifactId) {
        final File rootDir = context.getExecutionRootDirectory();
        final String pomPath = rootDir.getAbsolutePath() + File.separator + artifactId;
        File pomRoot = new File(pomPath);
        if (!rootDir.exists() || !dockerModuleFoundPom(pomRoot)) {
            logger.warn("Unable to find Docker module. Will not be able to direct manual updates for the deploy module's POM.xml");
        } else {
            String pomFilePath = pomPath + File.separator + trainingDockerArtifactId;
            boolean repoUrlExists = existsInFile(pomFilePath,"<repoUrl>" + "ECR_REPO_URL" + "</repoUrl>");
            boolean imageNameExists = existsInFile(pomFilePath, "<imageName>" + "ECR_REPO_URL" + "/${dockerImageName}</imageName>");
            if (repoUrlExists || imageNameExists) {
                final String key = getMessageKey(pomFilePath, "pom");
                VelocityNotification notification = new VelocityNotification(key, new HashSet<>(), "templates/notifications/notification.sagemaker.docker.pom.vm");
                notification.addToVelocityContext("artifactId", artifactId);
                notification.addToVelocityContext("dockerArtifactId", trainingDockerArtifactId);
                notification.addToVelocityContext("repoUrlExists", repoUrlExists);
                notification.addToVelocityContext("imageNameExists", imageNameExists);
                addManualAction(pomFilePath, notification);
            }
        }
    }

    private boolean executionAppExistsInPomFile(File file, String appName) {
        return propertyVariableExistsInPomFile(file, appName, "appName", appName);
    }

    private boolean propertyVariableExistsInPomFile(File file, String appName, String propertyVariableName, String propertyVariableValue) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        boolean exists = false;
        if (file.exists() && !file.isDirectory()) {
            try {
                db = dbf.newDocumentBuilder();

                Document doc = db.parse(file);
                XPath xPath = XPathFactory.newInstance().newXPath();
                String expression = "/project/build/plugins/plugin/executions/execution";
                NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(
                        doc, XPathConstants.NODESET);
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node nNode = nodeList.item(i);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Node cNode = getChildNodeByName(nNode, "configuration");
                        cNode = getChildNodeByName(cNode, "propertyVariables");
                        if (cNode != null) {
                            Node appNode = getChildNodeByName(cNode, "appName");
                            if (appNode != null && appName.equals(appNode.getTextContent())) {
                                Node pvNode = getChildNodeByName(cNode, propertyVariableName);
                                if (pvNode != null && propertyVariableValue.equals(pvNode.getTextContent())) {
                                    exists = true;
                                }
                            }
                        }
                    }
                }

            } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
                logger.error("Error while trying to find propertyVariable {} for appName {}", propertyVariableName, appName, e);
            }
        }
        return exists;
    }

    private Node getChildNodeByName(Node nNode, String name) {
        Element eElement = (Element) nNode;
        if (eElement != null) {
            return eElement.getElementsByTagName(name).item(0);
        }
        return null;
    }

    /**
     * Adds a notification to update the {@code pyproject.toml} configuration.
     *
     * @param context      the generation context
     * @param dependencies the dependencies to add
     * @param description  description of what the dependencies are for
     */
    public void addNoticeToAddPythonDependencies(GenerationContext context, Set<String> dependencies, String description) {

        final String pyprojectFilePath = context.getProjectDirectory() + File.separator + "pyproject.toml";
        final Set<String> dependenciesToAdd = new LinkedHashSet<>();
        for (String dependency : dependencies) {
            if (!existsInFile(pyprojectFilePath, dependency)) {
                dependenciesToAdd.add(dependency);
            }
        }

        if (CollectionUtils.isNotEmpty(dependenciesToAdd)) {
            final String key = getMessageKey(pyprojectFilePath, "requirement");

            HashSet<String> items = new HashSet<String>();
            for (String dependency : dependenciesToAdd) {
                items.add(dependency);
            }

            VelocityNotification notification = new VelocityNotification(key, items, "templates/notifications/notification.python.dependencies.vm");
            notification.addToVelocityContext("description", description);
            notification.addToVelocityContext("artifactId", context.getArtifactId());
            addManualAction(pyprojectFilePath, notification);
        }

    }

    /**
     * Adds a notification to update the s3-local deploy values.yaml file with a required object
     *
     * @param context     the generation context
     * @param objectNames the name of the object to add
     */
    public void addNoticeToUpdateS3LocalConfig(final GenerationContext context, final String bucketName, final List<String> objectNames) {
        final File rootDir = context.getExecutionRootDirectory();
        if (rootDir.exists() && deployModuleFoundPom(rootDir)) {
            Path rootPath = rootDir.toPath();
            Path s3ValuesPath = rootPath.resolve(
                    Paths.get(MavenUtil.getDeployModuleName(rootDir),
                            "src", "main", "resources",
                            "apps", "s3-local", "values.yaml")).toAbsolutePath().normalize();
            try {
                if (showWarnings(s3ValuesPath.toString())) {
                    if (!existsInFile(s3ValuesPath.toString(), "- name: " + bucketName)) {

                        HashSet<String> items = new HashSet<String>();
                        for (String objectName : objectNames) {
                            items.add(objectName);
                        }
                        final String key = getMessageKey(s3ValuesPath.toString(), bucketName, "buckets");
                        VelocityNotification notification = new VelocityNotification(key, items, "templates/notifications/notification.s3.local.buckets.vm");
                        notification.addToVelocityContext("rootPath", rootPath.relativize(s3ValuesPath).toString());
                        notification.addToVelocityContext("bucketName", bucketName);
                        addManualAction(s3ValuesPath.toString(), notification);
                    } else {
                        HashSet<String> items = new HashSet<String>();
                        boolean pathExistsInFile = false;
                        for (String objectName : objectNames) {
                            if (!existsInFile(s3ValuesPath.toString(), "- " + objectName)) {
                                items.add(objectName);
                                pathExistsInFile = true;
                            }
                        }

                        if (pathExistsInFile) {
                            final String key = getMessageKey(s3ValuesPath.toString(), bucketName, "bucketobjects");

                            VelocityNotification notification = new VelocityNotification(key, items, "templates/notifications/notification.s3.local.bucketobjects.vm");
                            notification.addToVelocityContext("rootPath", rootPath.relativize(s3ValuesPath).toString());
                            notification.addToVelocityContext("bucketName", bucketName);
                            addManualAction(s3ValuesPath.toString(), notification);
                        }
                    }
                }
            } catch (GenerationException e) {
                logger.warn("Failed to validate s3-local buckets and objects. Check that the {} file exists", s3ValuesPath);
            }
        }
    }

    /**
     * Adds a notification to update the Kafka deploy values.yaml file with step messaging topics
     *
     * @param context   the generation context
     * @param topicName the kafka topic name to add
     */
    public void addNoticeToUpdateKafkaConfig(final GenerationContext context, final String topicName) {
        final File rootDir = context.getExecutionRootDirectory();
        if (rootDir.exists() && deployModuleFoundPom(rootDir)) {
            Path rootPath = rootDir.toPath();
            Path kafkaValuesPath = rootPath.resolve(
                    Paths.get(rootPath.getFileName() + "-deploy",
                            "src", "main", "resources",
                            "apps", "kafka-cluster", "values.yaml")).toAbsolutePath().normalize();
            try {
                if (showWarnings(kafkaValuesPath.toString())
                        && !existsInFile(kafkaValuesPath.toString(), topicName + ":")
                        && existsInFile(kafkaValuesPath.toString(), "KAFKA_CREATE_TOPICS")) {
                    final String key = getMessageKey(kafkaValuesPath.toString(), "topics");

                    HashSet<String> items = new HashSet<String>();
                    items.add(topicName);

                    VelocityNotification notification = new VelocityNotification(key, items, "templates/notifications/notification.kafka.config.vm");
                    notification.addToVelocityContext("rootPath", rootPath.relativize(kafkaValuesPath).toString());
                    addManualAction(kafkaValuesPath.toString(), notification);
                }
            } catch (GenerationException e) {
                logger.warn("Failed to validate Kafka topics. Check that the {} file exists", kafkaValuesPath);
            }
        }
    }

    private boolean showWarnings(String filePath) {
        return !existsInFile(filePath, SUPPRESS_WARNINGS);
    }

    private boolean existsInFile(final String filePath, final String text) {
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            return stream.anyMatch(lines -> lines.contains(text));
        } catch (IOException e) {
            throw new GenerationException("Could not introspect file: " + filePath, e);
        }
    }

    private String getMessageKey(final String... keyComponents) {
        if (keyComponents.length > 0) {
            return String.join("_", keyComponents);
        }
        return "";
    }

    private boolean moduleFoundPom(final File rootProjectDirectory, String moduleName) {
        return moduleName != null && MavenUtil.fileExists(rootProjectDirectory, moduleName);
    }

    private boolean deployModuleFoundPom(final File rootProjectDirectory) {
        return moduleFoundPom(rootProjectDirectory, MavenUtil.getDeployModuleName(rootProjectDirectory));
    }

    private boolean dockerModuleFoundPom(final File rootProjectDirectory) {
        return moduleFoundPom(rootProjectDirectory, MavenUtil.getDockerModuleName(rootProjectDirectory));
    }

    private boolean tiltFileFound(final File rootProjectDirectory) {
        return MavenUtil.fileExists(rootProjectDirectory, "Tiltfile");
    }

    private void addManualAction(String file, Notification notification) {
        NotificationCollector.addNotification(file, notification);
    }

    /**
     * Check if the appName already exists in the notification items with the given file and key.
     *
     * @param file    the file the notification applies to
     * @param key     the key of the notification to be added
     * @param appName the application name to add
     */
    public boolean hasNotificationWithAppName(final String file, final String key, final String appName) {
        final Map<String, Notification> notificationsForFile = NotificationCollector.getNotifications().
                computeIfAbsent(file, m -> new ConcurrentHashMap<>());
        boolean found = false;
        if (notificationsForFile.containsKey(key)) {
            Set<String> items = notificationsForFile.get(key).getItems();
            for (String item : items) {
                if (item.contains("<appName>" + appName + "</appName>")) {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    /**
     * Check if the execution item with specific appName already exists in the notification with given file and the key
     *
     * @param file    the file the notification applies to
     * @param key     the key of the notification to be added
     * @param appName the application name to add
     */
    private final boolean executionAppExistsInNotification(final String file, final String key, final String appName) {
        return hasNotificationWithAppName(file, key, appName);
    }

    /**
     * Performs some common logic to set up information for building notifications to update pom files.
     *
     * @param optionalRoot The root directory of the files needed for this notification
     * @param profile      The generation profile
     * @param appName      The app name to add to the pom file
     * @param getModule    A function to find the artifact id for the notification, varies by module type
     * @return An object containing the parameters needed for the rest of the notification generation
     */
    private NotificationParams configureNotification(File optionalRoot, String profile, String appName, Function<File, String> getModule) {
        NotificationParams params = new NotificationParams();

        params.setArtifactId(getModule.apply(optionalRoot));
        final String deployDir = optionalRoot.getAbsolutePath() + File.separator + params.getArtifactId();
        params.setPomFilePath(deployDir + File.separator + "pom.xml");
        params.setProfileConfiguration("<profile>" + profile + "</profile>");
        params.setPomFile(new File(params.getPomFilePath()));
        params.setKey(getMessageKey(params.getPomFilePath(), "execution", appName));
        params.setExistsInFileOrNotification(executionAppExistsInPomFile(params.getPomFile(), appName) || executionAppExistsInNotification(params.getPomFilePath(), params.getKey(), appName));

        return params;
    }

    /**
     * Helper class containing the parameters for configuring notifications
     */
    private class NotificationParams {
        private String artifactId;
        private String pomFilePath;
        private String profileConfiguration;
        private File pomFile;
        private String key;
        private boolean existsInFileOrNotification;

        public String getArtifactId() {
            return artifactId;
        }

        public void setArtifactId(String artifactId) {
            this.artifactId = artifactId;
        }

        public String getPomFilePath() {
            return pomFilePath;
        }

        public void setPomFilePath(String pomFilePath) {
            this.pomFilePath = pomFilePath;
        }

        public String getProfileConfiguration() {
            return profileConfiguration;
        }

        public void setProfileConfiguration(String profileConfiguration) {
            this.profileConfiguration = profileConfiguration;
        }

        public File getPomFile() {
            return pomFile;
        }

        public void setPomFile(File pomFile) {
            this.pomFile = pomFile;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public boolean isExistsInFileOrNotification() {
            return existsInFileOrNotification;
        }

        public void setExistsInFileOrNotification(boolean existsInFileOrNotification) {
            this.existsInFileOrNotification = existsInFileOrNotification;
        }
    }

}
