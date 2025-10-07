package com.boozallen.aiops.mda;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
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

import com.boozallen.aiops.mda.generator.util.MavenUtil;
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import org.apache.commons.collections4.CollectionUtils;

import org.apache.commons.lang3.StringUtils;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Class to aid in notifying users of required actions.
 */
@Named
public class ManualActionNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(ManualActionNotificationService.class);
    private static final String EMPTY_LINE = "\n";
    private static final String SUPPRESS_WARNINGS = "maven-suppress-warnings";
    private static final String GROUP_HELMFILE_APPS = "helmfile-apps";
    private static final String GROUP_HELMFILE = "helmfile";
    private static final String APP_NAME = "appName";
    private static final String CONFIGURATION_STORE = "configuration-store";
    private static final Map<String, String> HELMFILE_CONDITIONALS = Collections.singletonMap("s3-local", "helm.s3local.enabled");
    private static final Map<String, String> HELMFILE_NAMESPACES = Collections.singletonMap(CONFIGURATION_STORE, "-config");

    public void addSchemaElementDeprecationNotice(String illegalElement, String objectType) {
        final String SCHEMA_ELEMENT_DEPRECATION_KEY = "schema_element_deprecation";

        VelocityNotification notification = new VelocityNotification(getMessageKey(SCHEMA_ELEMENT_DEPRECATION_KEY, illegalElement), "schemaelementdeprecation", new HashSet<String>(), "templates/notifications/notification.schema.element.deprecation.vm");
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
        final String pomFilePath = context.getProjectDirectory() + File.separator + "pom.xml";
        final String query = "<module>" + artifactId + "</module>";
        boolean alreadyExists = existsInFile(pomFilePath, query);

        String relativePomFilePath = getRelativePathToProjectRoot(context.getExecutionRootDirectory(), new File(pomFilePath));

        if (!alreadyExists) {
            final String key = getMessageKey(relativePomFilePath, "module");

            HashSet<String> items = new HashSet<String>();
            items.add("<module>" + (artifactId) + "</module>");

            VelocityNotification notification = new VelocityNotification(key, items, "templates/notifications/notification.module.to.parent.vm");
            notification.addToVelocityContext("moduleType", moduleType);
            notification.addToVelocityContext("displayPomFilePath", relativePomFilePath);
            notification.addToVelocityContext("artifactId", artifactId);
            addManualAction(pomFilePath, notification);
        }

    }

    /**
     * Checks if updates are needed to dependencies in a POM file.
     *
     * @param context    the generation context
     * @param artifactId  the artifact ID
     * @param persistType the persist type
     */
    public void addNoticeToAddDependency(GenerationContext context, String artifactId, String persistType) {

        final String pomFilePath = context.getProjectDirectory() + File.separator + artifactId + File.separator + "pom.xml";
        boolean alreadyExists = existsInFile(pomFilePath, String.format("extensions-data-delivery-spark-%s", persistType));

        if (!alreadyExists) {
            String relativePomFilePath = getRelativePathToProjectRoot(context.getExecutionRootDirectory(), new File(pomFilePath));
            final String key = getMessageKey(relativePomFilePath, "extensions-data-delivery-spark", persistType);

            VelocityNotification notification = new VelocityNotification(key, new HashSet<>(), "templates/notifications/notification.dependency.vm");
            notification.addToVelocityContext("dependencyArtifactId", String.format("extensions-data-delivery-spark-%s", persistType));
            notification.addToVelocityContext("artifactId", artifactId);
            addManualAction(pomFilePath, notification);
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
                    getMessageKey("pom.xml", "set-habushu-dist-artifact-version"),
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
        final File rootDir = context.getExecutionRootDirectory();
        final String deployPom = rootDir.getAbsolutePath() + File.separator + deployArtifactId +  File.separator + "pom.xml";

        if(deployArtifactId != null && !existsInFile(deployPom, "<artifactId>maven-clean-plugin</artifactId>")) {
            String relativePomFilePath = getRelativePathToProjectRoot(rootDir, new File(deployPom));
            VelocityNotification notification = new VelocityNotification(
                    getMessageKey(relativePomFilePath, "clean-deploy-apps-targets"),
                    "deploy-plugins",
                    new HashSet<>(),
                    "templates/notifications/notification.deploy.clean.app.target.vm"
            );
            addManualAction(deployPom, notification);
        }
    }

    /**
     * Manual action for adding the pipeline invocation service deployment execution and all associated plugin
     * executions.
     * @param context generation context
     */
    public void addPipelineInvocationServiceDeployment(final GenerationContext context) {
        final File rootDir = context.getExecutionRootDirectory();
        String deployArtifactId = MavenUtil.getDeployModuleName(rootDir);
        final String deployPom = rootDir.getAbsolutePath() + File.separator + deployArtifactId + File.separator + "pom.xml";

        if(deployArtifactId != null && !existsInFile(deployPom, "<artifactId>mda-maven-plugin</artifactId>")) {
            String relativePomFilePath = getRelativePathToProjectRoot(rootDir, new File(deployPom));
            VelocityNotification notification = new VelocityNotification(
                    getMessageKey(relativePomFilePath, "pipeline-invocation-service-spark-apps"),
                    "deploy-plugins",
                    new HashSet<>(),
                    "templates/notifications/notification.mda.maven.pipeline.invocation.execution.vm"
            );
            notification.addToExternalVelocityContextProperties("deployArtifactId", deployArtifactId);
            addManualAction(deployPom, notification);
            addDeployPomMessage(context, "pipeline-invocation-service-v2", "pipeline-invocation-service");
        }
        addCleanPluginNotification(deployArtifactId, context);
        addHabushuRegexPluginInvocation(context);
    }

    /**
     * Adds a notification to update the helmfile with releases.
     *
     * @param context          the generation context
     * @param appName          the application name
     * @param deployArtifactId the deploy artifact ID
     */
    public void addHelmfileReleaseMessage(final GenerationContext context, final String appName,
                                       final String deployArtifactId, String projectName) {

        final File rootDir = context.getExecutionRootDirectory();
        if (!rootDir.exists() || !helmfileFound(rootDir, true)) {
            logger.warn("Unable to find helmfile.yaml.gotmpl. Will not be able to direct manual release updates for " +
                    "helmfile.");
        } else {
            final String helmfilePath = rootDir.toPath().resolve("helmfile-apps.yaml.gotmpl").toString();
            final String text = "apps/" + appName;

            boolean helmfileContainsArtifact = existsInFile(helmfilePath, text);
            if (!helmfileContainsArtifact && showWarnings(helmfilePath)) {
                final String key = getMessageKey("helmfile-apps", "release", appName);
                VelocityNotification notification = new VelocityNotification(key, GROUP_HELMFILE_APPS, new HashSet<>(),
                        "templates/notifications/notification.helm.helmfile.vm");

                Map<String, String> helmfileNeeds = getHelmfileNeeds(projectName);
                // All deployments have a "need" on the configuration store to be up first
                if(!StringUtils.equals(appName, CONFIGURATION_STORE)) {
                    notification.addToVelocityContext("needs",
                            Collections.singletonList(helmfileNeeds.get(CONFIGURATION_STORE)));
                }

                // Some releases have conditions to be disabled for certain environments
                if (HELMFILE_CONDITIONALS.containsKey(appName)) {
                    notification.addToVelocityContext("condition", HELMFILE_CONDITIONALS.get(appName));
                }

                notification.addToVelocityContext("namespace", projectName);
                // Some releases have different namespaces
                if (HELMFILE_NAMESPACES.containsKey(appName)) {
                    notification.addToVelocityContext("namespace", projectName + HELMFILE_NAMESPACES.get(appName));
                }

                // These applications need to have helm wait until they are up before moving on. This is because they
                // are needed by other releases
                if (helmfileNeeds.containsKey(appName)) {
                    notification.addToVelocityContext("wait", true);
                }

                notification.addToVelocityContext(APP_NAME, appName);
                notification.addToVelocityContext("deployArtifactId", deployArtifactId);
                addManualAction(helmfilePath, notification);
            }
        }
    }

    /**
     * Adds a notification to update the helmfile with necessary spark worker releases
     *
     * @param context                the generation context
     * @param parentArtifactId       the name of the parent directory the pipelines are in
     * @param pipelineArtifactId     the artifact id of the pipeline
     * @param pipelineImplementation the implementation of the pipeline
     */
    public void addSparkWorkerHelmfileRelease(final GenerationContext context, final String parentArtifactId,
                                            final String pipelineArtifactId, final String pipelineImplementation,
                                              String projectName) {

        final File rootDir = context.getExecutionRootDirectory();
        if (!rootDir.exists() || !helmfileFound(rootDir, false)) {
            logger.warn("Unable to find helmfile.yaml.gotmpl. Will not be able to direct Spark Worker manual release updates" +
                    " to for helmfile.");
        } else {
            final String helmfilePath = rootDir.toPath().resolve("helmfile.yaml.gotmpl").toString();
            final String text = parentArtifactId + "/" + pipelineArtifactId;

            boolean helmfileContainsArtifact = existsInFile(helmfilePath, text);
            if (!helmfileContainsArtifact && showWarnings(helmfilePath)) {
                final String key = getMessageKey("helmfile", "release", pipelineArtifactId);

                VelocityNotification notification = new VelocityNotification(key, GROUP_HELMFILE, new HashSet<>(),
                        "templates/notifications/notification.spark.worker.helmfile.vm");
                notification.addToVelocityContext("parentArtifactId", parentArtifactId);
                notification.addToVelocityContext("pipelineArtifactId", pipelineArtifactId);
                notification.addToVelocityContext("pipelineImplementation", pipelineImplementation);
                notification.addToVelocityContext("pythonPipelineArtifactId", PipelineUtils.deriveLowerSnakeCaseNameFromHyphenatedString(pipelineArtifactId));
                notification.addToVelocityContext("namespace", projectName);
                Map<String, String> helmfileNeeds = getHelmfileNeeds(projectName);
                notification.addToVelocityContext("needs", helmfileNeeds.values());
                addManualAction(helmfilePath, notification);
            }
        }
    }

    /**
     * Adds a notification to update the pom.xml for the docker module.
     *
     * @param context         the generation context
     * @param profile         the profile to add
     * @param appName         the application name to add
     */
    public void addDeployPomMessage(final GenerationContext context, final String profile, final String appName) {
        final File rootDir = context.getExecutionRootDirectory();
        if (!rootDir.exists() || !deployModuleFoundPom(rootDir)) {
            logger.warn("Unable to find Docker module. Will not be able to direct manual updates for the deploy module's POM.xml");
        } else {
            if (StringUtils.isNotEmpty(profile)) {
                NotificationParams params = configureNotification(rootDir, profile, appName, MavenUtil::getDeployModuleName);

                if (!params.isExistsInFileOrNotification()) {

                    VelocityNotification notification = new VelocityNotification(params.getKey(), "deploypom",
                            new HashSet<>(), "templates/notifications/notification.deploy.pom.vm");
                    notification.addToVelocityContext(APP_NAME, appName);
                    notification.addToVelocityContext(("profile"), profile);
                    notification.addToVelocityContext("basePackage", context.getBasePackage());
                    notification.addToVelocityContext("profileConfiguration", params.getProfileConfiguration());

                    notification.addToExternalVelocityContextProperties("deployArtifactId", params.getArtifactId());

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
                    notification.addToVelocityContext(APP_NAME, artifactId);
                    notification.addToVelocityContext("basePackage", context.getBasePackage());
                    notification.addToVelocityContext("profileConfiguration", params.getProfileConfiguration());
                    notification.addToExternalVelocityContextProperties("dockerArtifactId", params.getArtifactId());

                    addManualAction(params.getPomFilePath(), notification);
                }
            }
        }
    }

    /**
     * Adds a manual action notification to configure an outbound SmallRye connector for a specific channel to support
     * sending messages to an external messaging system.
     *
     * @param context the Fermenter generation context
     * @param pipelineArtifactId the artifact ID for the pipeline which needs an outbound connector configured
     * @param description the plain English description of the messaging channel, e.g. "Alert Producer"
     * @param channel the SmallRye channel name on which the connector will receive outbound messages
     * @param serializerClass the class used to serialize the message values
     */
    public void addSmallRyeConnectorMessage(GenerationContext context, String pipelineArtifactId, String description, String channel, String serializerClass) {
        File root = context.getExecutionRootDirectory();
        String pipelinesModule = MavenUtil.getPipelinesModuleName(root);
        if (root != null && pipelinesModule != null) {
            Path pipelineDir = root.toPath().resolve(Path.of(pipelinesModule, pipelineArtifactId));
            Path mpConfig = pipelineDir.resolve("src/main/resources/META-INF/microprofile-config.properties");
            String connectorProperty = "mp.messaging.outgoing." + channel + ".connector=";

            if (Files.exists(mpConfig) && !existsInFile(mpConfig.toString(), connectorProperty)) {
                String key = getMessageKey(pipelineArtifactId, "microprofile-config", channel, "outgoing-connector");
                VelocityNotification notification = new VelocityNotification(key, "microprofile-config", new HashSet<>(),
                        "templates/notifications/notification.microprofile-config.connector.vm");
                notification.addToVelocityContext("description", description);
                notification.addToVelocityContext("channel", channel);
                notification.addToVelocityContext("topic", channel);
                notification.addToVelocityContext("serializer", serializerClass);
                // Group Template properties
                notification.addToExternalVelocityContextProperties("pipelinesArtifactId", pipelinesModule);
                notification.addToExternalVelocityContextProperties("pipelineArtifactId", pipelineArtifactId);
                addManualAction(mpConfig.toString(), notification);
            }
        }
    }
    
    private boolean executionAppExistsInPomFile(File file, String appName) {
        return propertyVariableExistsInPomFile(file, appName, APP_NAME, appName);
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
                            Node appNode = getChildNodeByName(cNode, APP_NAME);
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
            String relativePyprojectFilePath = getRelativePathToProjectRoot(context.getExecutionRootDirectory(), context.getProjectDirectory()) + File.separator + "pyproject.toml";
            final String key = getMessageKey(relativePyprojectFilePath, "requirement", context.getArtifactId());

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
                    String relativeValuesFilePath = getRelativePathToProjectRoot(rootDir, s3ValuesPath.toFile());

                    if (!existsInFile(s3ValuesPath.toString(), "- name: " + bucketName)) {

                        HashSet<String> items = new HashSet<String>();
                        for (String objectName : objectNames) {
                            items.add(objectName);
                        }
                        final String key = getMessageKey(relativeValuesFilePath, "buckets", bucketName);
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
                            final String key = getMessageKey(relativeValuesFilePath, "bucketobjects", bucketName);

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
                    
                    String relativeValuesFilePath = getRelativePathToProjectRoot(rootDir, kafkaValuesPath.toFile());
                    final String key = getMessageKey(relativeValuesFilePath, "kafka-topics");

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

    /**
     * Returns the path of the current project directory relative to the project root.
     * Given:
     *   my/test/project
     *   my/test/project/submodule1/submodule2
     * Returns:
     *   submodule1/submodule2
     * 
     * @param projectRoot The root directory
     * @param currentDirectory The current directory
     * @return The relative path as a {@link String}
     */
    private String getRelativePathToProjectRoot(File projectRoot, File currentDirectory) {
        return projectRoot.toPath().relativize(currentDirectory.toPath()).toString();
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

    private boolean helmfileFound(final File rootProjectDirectory, final boolean isApps) {
        String filename = isApps? "helmfile-apps.yaml.gotmpl": "helmfile.yaml.gotmpl";
        return MavenUtil.fileExists(rootProjectDirectory, filename);
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
     * @param rootDir      The root directory of the files needed for this notification
     * @param profile      The generation profile
     * @param appName      The app name to add to the pom file
     * @param getModule    A function to find the artifact id for the notification, varies by module type
     * @return An object containing the parameters needed for the rest of the notification generation
     */
    private NotificationParams configureNotification(File rootDir, String profile, String appName, Function<File, String> getModule) {
        NotificationParams params = new NotificationParams();

        params.setArtifactId(getModule.apply(rootDir));
        final String deployDir = rootDir.getAbsolutePath() + File.separator + params.getArtifactId();

        params.setPomFilePath(deployDir + File.separator + "pom.xml");
        params.setProfileConfiguration("<profile>" + profile + "</profile>");
        params.setPomFile(new File(params.getPomFilePath()));

        String relativePomFilePath = getRelativePathToProjectRoot(rootDir, params.getPomFile());
        params.setKey(getMessageKey(relativePomFilePath, "execution", appName));

        params.setExistsInFileOrNotification(executionAppExistsInPomFile(params.getPomFile(), appName) || executionAppExistsInNotification(params.getPomFilePath(), params.getKey(), appName));

        return params;
    }

    /**
     * Creates a map containing the `needs` for helmfile releases. Key is appName and Value is the helmfile needs
     * @param projectName the name of the project. Used to derive the namespace
     * @return The map of appNames and needs value
     */
    private Map<String, String> getHelmfileNeeds(String projectName){
        Map<String, String> helmfileNeeds = new HashMap<>();
        helmfileNeeds.put(CONFIGURATION_STORE, projectName + "-config/configuration-store");
        helmfileNeeds.put("spark-operator", projectName + "/spark-operator");
        helmfileNeeds.put("spark-infrastructure", projectName + "/spark-infrastructure");
        return helmfileNeeds;
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
