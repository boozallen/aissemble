# Major Additions

## OpenLineage Namespace Conventions
Conventions for setting namespaces when leveraging `Data Lineage` has been updated to better follow [OpenLineage's guidelines](https://openlineage.io/docs/spec/naming/). Moving forward, namespaces should be defined in the `data-lineage.properties` file, such that Jobs are tied to pipelines and Datasets are tied to data sources. This is a departure from the old pattern of one single namespace property (`data.lineage.namespace`) being leveraged for an entire project. Refer to the [GitHub docs](https://boozallen.github.io/aissemble/current-dev/lineage-medatada-capture-overview.html#_configuration) for updated guidance. Usage of the `data.lineage.namespace` property in a project's `data-lineage.properties` file will be supported as a fallback but should not be used in practice.

## Maven Build Cache
The [Maven Build Cache](https://maven.apache.org/extensions/maven-build-cache-extension/) is now enabled by default for new projects.  Existing projects can reference [the generation template](https://github.com/boozallen/aissemble/tree/dev/foundation/foundation-archetype/src/main/resources/archetype-resources/.mvn) to enable this functionality in their own projects.

## Kafka Docker Image
The baseline Kafka Docker image has moved away from using the _wurstmeister/kafka_ image (which was outdated and is no longer available) to using [Bitnami's Kafka image](https://hub.docker.com/r/bitnami/kafka) as its base.  If you are using the v2 Kafka chart managed by aiSSEMBLE, it will now pull the baseline Kafka image instead of directly using the Bitnami image. If you are still on the older v1 chart, it is already using the baseline image and will be the Bitnami flavor in 1.7.0. Kafka Connect support is still included in the baseline image.

## Package Renaming
* Python modules were renamed to reflect aiSSEMBLE. These include the following.

| Old Python Module                       | New Python Module                                 |
|-----------------------------------------|---------------------------------------------------|
| foundation-core-python                  | aissemble-core-python                             |
| foundation-model-training-api           | aissemble-foundation-model-training-api           |
| foundation-versioning-service           | aissemble-foundation-versioning-service           |
| foundation-drift-detection-client       | aissemble-foundation-drift-detection-client       |
| foundation-encryption-policy-python     | aissemble-foundation-encryption-policy-python     |
| foundation-model-lineage                | aissemble-foundation-model-lineage                |
| foundation-data-lineage-python          | aissemble-foundation-data-lineage-python          |
| foundation-messaging-python-client      | aissemble-foundation-messaging-python-client      |
| foundation-pdp-client-python            | aissemble-foundation-pdp-client-python            |
| foundation-transform-core-python        | aissemble-Foundation-transform-core-python        |
| extensions-model-training-api-sagemaker | aissemble-extensions-model-training-api-sagemaker |
| extensions-data-delivery-spark-py       | aissemble-extensions-data-delivery-spark-py       |
| extensions-encryption-vault-python      | aissemble-extensions-encryption-vault-python      |
| extensions-transform-spark-python       | aissemble-extensions-transform-spark-python       |
| test-data-delivery-pyspark-model        | aissemble-test-data-delivery-pyspark-model        |
| test-data-delivery-pyspark-model-basic  | aissemble-test-data-delivery-pyspark-model-basic  |
| machine-learning-inference              | aissemble-machine-learning-inference              |
| machine-learning-training               | aissemble-machine-learning-training               |
| machine-learning-training-base          | aissemble-machine-learning-training-base          |
| machine-learning-sagemaker-training     | aissemble-machine-learning-sagemaker-training     |

* Helm Charts and their relevant modules have been renamed to the following:

| Old Module Name                         | New Helm Chart and Module Name          |
|-----------------------------------------|-----------------------------------------|
| extensions-helm-airflow                 | aissemble-airflow-chart                 |
| extensions-helm-data-access             | aissemble-data-access-chart             |
| extensions-helm-elasticsearch           | aissemble-elasticsearch-chart           |
| extensions-helm-elasticsearch-operator  | aissemble-elasticsearch-operator-chart  |
| extensions-helm-fastapi                 | aissemble-fastapi-chart                 |
| extensions-helm-hive-metastore-db       | aissemble-hive-metastore-db-chart       |
| extensions-helm-hive-metastore-service  | aissemble-hive-metastore-service-chart  |
| extensions-helm-inference               | aissemble-inference-chart               |
| extensions-helm-jenkins                 | aissemble-jenkins-chart                 |
| extensions-helm-kafka                   | aissemble-kafka-chart                   |
| extensions-helm-keycloak                | aissemble-keycloak-chart                |
| extensions-helm-lineage-http-consumer   | aissemble-lineage-http-consumer-chart   |
| extensions-helm-localstack              | aissemble-localstack-chart              |
| extensions-helm-metadata                | aissemble-metadata-chart                |
| extensions-helm-mlflow                  | aissemble-mlflow-chart                  |
| extensions-helm-pipeline-invocation     | aissemble-pipeline-invocation-chart     |
| extensions-helm-pipeline-invocation-lib | aissemble-pipeline-invocation-lib-chart |
| extensions-helm-policy-decision-point   | aissemble-policy-decision-point-chart   |
| extensions-helm-quarkus                 | aissemble-quarkus-chart                 |
| extensions-helm-sealed-secrets          | aissemble-sealed-secrets-chart          |
| extensions-helm-spark-application       | aissemble-spark-application-chart       |
| extensions-helm-spark-operator          | aissemble-spark-operator-chart          |
| extensions-helm-vault                   | aissemble-vault-chart                   |
| extensions-helm-versioning              | aissemble-versioning-chart              |

# Breaking Changes
Note instructions for adapting to these changes are outlined in the upgrade instructions below.
* The maven property `version.clean.plugin` was changed to `version.maven.clean.plugin` causing the `*-deploy/pom.xml` to be invalid.
* The specification of private maven repositories has been changed from prior releases.
* The specification of private PyPI repositories has been changed from prior releases.
* The specification of private docker repository has been changed from prior releases.
* The specification of Helm publishing repositories has been changed from prior releases.
* The Kafka home directory in the **aissemble-kafka** image has changed from _/opt/kafka_ to _/opt/bitnami/kafka_

# Known Issues
There are no known issues with the 1.7.0 release.

# Known Vulnerabilities
| Date<br/>identified | Vulnerability | Severity | Package | Affected <br/>versions | CVE | Fixed <br/>in |
| ------------------- | ------------- | -------- | ------- | ---------------------- | --- | ------------- |

# How to Upgrade
The following steps will upgrade your project to 1.7. These instructions consist of multiple phases:
- Automatic Upgrades - no manual action required
- Precondition Steps - needed in all situations
- Conditional Steps (e.g., Python steps, Java steps, if you use Metadata, etc)
- Final Steps - needed in all situations

## Automatic Upgrades
To reduce burden of upgrading aiSSEMBLE, the Baton project is used to automate the migration of some files to the new version.  These migrations run automatically when you build your project, and are included by default when you update the `build-parent` version in your root POM.  Below is a description of all of the Baton migrations that are included with this version of aiSSEMBLE.

| Migration Name                                             | Description                                                                                                                                                                                                                                            |
|------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| upgrade-tiltfile-aissemble-version-migration               | Updates the aiSSEMBLE version within your project's Tiltfile                                                                                                                                                                                           |
| upgrade-v2-chart-files-aissemble-version-migration         | Updates the helm chart dependencies within your project's deployment resources (<YOUR_PROJECT>-deploy/src/main/resources/apps/) to use the latest version of the aiSSEMBLE                                                                             |
| upgrade-v1-chart-files-aissemble-version-migration         | Updates the docker image tags within your project's deployment resources (<YOUR_PROJECT>-deploy/src/main/resources/apps/) to use the latest version of the aiSSEMBLE                                                                                   |
| upgrade-mlflow-v2-external-s3-migration                    | Update the mlflow V2 deployment (if present) in your project to utilize Localstack for local development and SealedSecrets for remote deployments                                                                                                      |
| upgrade-spark-application-s3-migration                     | Update the pipeline SparkApplication(s) (if present) in your project to utilize Localstack for local development and SealedSecrets for remote deployments                                                                                              |
| upgrade-foundation-extension-python-package-migration      | Updates the pyproject.toml files within your projects pipelines folder (<YOUR_PROJECT>-pipelines) to use the updated aiSSEMBLE foundation and extension Python packages with the latest naming convention                                              | 
| upgrade-helm-chart-names-migration                         | Updates the Chart.yaml and values*.yaml files within your project's deploy folder (<YOUR_PROJECT>-deploy) to use the new Helm chart naming convention (`aissemble-<chart-name>-chart`).                                                                |
| upgrade-helm-module-names-migration                        | Updates the Chart.yaml and values*.yaml files within your project's deploy folder (<YOUR_PROJECT>-deploy) to use the new Helm module naming convention (`aissemble-<chart-name>-chart`)                                                                |
| upgrade-helm-chart-repository-url-migration                | Updates the Helm repository URL within your project's deploy Chart.yaml file to point to ghcr.io. Only runs if the previous Helm chart repository URL is passed in through the `oldHelmRepositoryUrl` system property (using `-DoldHelmRepositoryUrl`) |
| upgrade-dockerfile-pip-install-migration                   | Updates dockerfiles such that python dependency installations fail during the build, rather than at runtime                                                                                                                                            |
| enable-habushu-build-cache-migration                       | Updates the `pom.xml` file for any Habushu-managed modules to ensure that the build directory is specified.                                                                                                                                            |
| data-lineage-package-import-migration                      | Updates the package imports for all java files that are referencing `com.boozallen.aissemble.data.lineage`.                                                                                                                                            |
| upgrade-spark-application-exec-migration.                  | Fixes the exec-maven-plugin executions in pipeline POMs to use the new ghcr.io aissemble-spark-application-chart package                                                                                                                               |
| upgrade-project-specific-image-naming-convention-migration | Updates the project specific aiSSEMBLE generated image names by removing the `boozallen/` prefix                                                                                                                                                       |

To deactivate any of these migrations, add the following configuration to the `baton-maven-plugin` within your root `pom.xml`:

```diff
    <plugin>
        <groupId>org.technologybrewery.baton</groupId>
        <artifactId>baton-maven-plugin</artifactId>
        <dependencies>
            <dependency>
                <groupId>com.boozallen.aissemble</groupId>
                <artifactId>foundation-upgrade</artifactId>
                <version>${version.aissemble}</version>
            </dependency>
        </dependencies>
+        <configuration>
+             <deactivateMigrations>
+                 <deactivateMigration>NAME_OF_MIGRATION</deactivateMigration>
+                 <deactivateMigration>NAME_OF_MIGRATION</deactivateMigration>
+             </deactivateMigrations>
+        </configuration>
    </plugin>
```

## Precondition Steps - Required for All Projects

### Beginning the Upgrade
To start your aiSSEMBLE upgrade, update your project's pom.xml to use the 1.7.0 version of the build-parent:
   ```xml
   <parent>
       <groupId>com.boozallen.aissemble</groupId>
       <artifactId>build-parent</artifactId>
       <version>1.7.0</version>
   </parent>
   ```

### Delete the Old maven-clean-plugin Version
In order to follow the standard naming conventions for maven properties, the original property used for the
maven-clean-plugin version no longer exists. To resolve the Maven failure this causes, delete the version from the
plugin in *-deploy/pom.xml. This can be achieved with:
```
sed -i'' -e '/version.clean.plugin/d' *-deploy/pom.xml
```

### Update Maven Repository Configuration
Update the following properties in your project's root `pom.xml` file with the appropriate Maven repository IDs and URLs
for publishing and retrieving releases and snapshots. Adjust for your project as appropriate:
```xml
<properties>
    ...
    <maven.repo.id>maven-releases</maven.repo.id>
    <maven.repo.url>https://release-PLACEHOLDER/repository/maven-releases</maven.repo.url>
    <maven.snapshot.repo.id>maven-snapshots</maven.snapshot.repo.id>
    <maven.snapshot.repo.url>https://snapshot-PLACEHOLDER/repository/maven-snapshots</maven.snapshot.repo.url>
</properties>
```

### Update Habushu to Point to Your Private PyPI Repository
Add the following `property` and `plugin` into your project's root `pom.xml` file with the appropriate PyPI repository URL (Nexus is used
in this example - adjust for your project, as appropriate):
```xml
  <properties>
    ...
    <pypi.project.repository.url>https://nexus.yourdomain/repository/your-pypi-repo-name/</pypi.project.repository.url>
  </properties>
  ...
  <build>
    <pluginManagement>
      <plugins>
        ...  
        <plugin>
          <groupId>org.technologybrewery.habushu</groupId>
          <artifactId>habushu-maven-plugin</artifactId>
          <configuration>
            <!--
              Ensure you have configured credentials for this repo, as explained in the following link:
              https://github.com/TechnologyBrewery/habushu?tab=readme-ov-file#pypirepoid
            -->
            <pypiRepoUrl>${pypi.project.repository.url}</pypiRepoUrl>
          </configuration>
        </plugin>
      </plugins>  
    </pluginManagement>
  </build>
```

### Add the Helm Publishing Repository Configuration
Add the following `property` into your project's root `pom.xml` file with the appropriate Helm repository URL and name you wish to publish your charts to (Nexus is used in this example - adjust for your project, as appropriate):

```xml
  <properties>
    ...
    <helm.publishing.repository.url>https://nexus.mydomain.com/repository</helm.publishing.repository.url>
    <helm.publishing.repository.name>my-helm-charts</helm.publishing.repository.name>
</properties>

```
Update the following `plugin` within your project's `-deploy/pom.xml` file. Adjust for your project as appropriate:
```xml
<plugin>
    <groupId>${group.helm.plugin}</groupId>
    <artifactId>helm-maven-plugin</artifactId>
    <executions>
        ...
        <execution>
            <id>deploy</id>
            <phase>deploy</phase>
            <goals>
                <goal>push</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        ...
        <uploadRepoStable>
            <name>${helm.publishing.repository.name}</name>
            <url>${helm.publishing.repository.url}</url>
        </uploadRepoStable>
        <uploadRepoSnapshot>
            <name>${helm.publishing.repository.name}</name>
            <url>${helm.publishing.repository.url}</url>
        </uploadRepoSnapshot>
        ...
    </configuration>
</plugin>

```
For further configurations that can be set for your specific Helm repository needs, please see the [helm-maven-plugin documentation](https://github.com/kokuwaio/helm-maven-plugin?tab=readme-ov-file)

### Update Docker Repository Configuration
Update the docker repository in your project's root `pom.xml` file with the appropriate docker repository URL
for publishing and retrieving docker images. Adjust for your project as appropriate:
```xml
<properties>
    ...
    <docker.project.repository.url>docker-registry-PLACEHOLDER/repository/</docker.project.repository.url>
</properties>
```
Additionally, add the docker repoId in the `orphedomos-maven-plugin` configuration in the project's `-docker/pom.xml` file.

### Update Tiltfile with New Docker Repository
Update the `build_args` defined in your `Tiltfile` to point `DOCKER_BASELINE_REPO_ID` to the `ghcr.io/` docker repository
```diff
+ build_args = { 'DOCKER_BASELINE_REPO_ID': 'ghcr.io/',
               'VERSION_AISSEMBLE': aissemble_version}
```

### Update v1 Helm Charts with New Docker Repository
Update the values.yaml file for any v1 helm charts in your `-deploy/apps` directory to point to the correct repository.

#### Github Container Registry Images
If you have a v1 chart that is included in this list
- `hive-metastore-db`
- `hive-metastore-service`
- `jenkins`
- `kafka`
- `metadata`
- `spark-infrastructure`

Then you will need to update the `dockerRepo` value in the corresponding `values.yaml` file to point to `"ghcr.io/"`:
```diff
image:
+   dockerRepo: "ghcr.io/"
```
#### Model Training
If you leverage `model-training-api` and/or `model-training-api-sagemaker`, update the image name to point to `ghcr.io/` like so:
```diff
image:
+  name: ghcr.io/boozallen/aissemble-model-training-api
```
for sagemaker:
```diff
image:
+  name: ghcr.io/boozallen/aissemble-model-training-api-sagemaker
```

#### Spark Operator
If you leverage `spark-operator`, you will need to update the `spark-operator`'s `values.yaml` file so the image repository points to `ghcr.io/` like so:
spark-operator:
```diff
image:
+  repository: "ghcr.io/boozallen/aissemble-spark-operator"
```

## Conditional Steps

## Upgrade Steps for Projects Leveraging the aiSSEMBLE Kafka Image
[REQUIRED]
If you are leveraging the **aissemble-kafka** image and are adding custom files to the Kafka home directory either through a Docker build or a Helm configuration, you will need to change the location of these files from _/opt/kafka/<location>_ to _/opt/bitnami/kafka/<location>_.

### Upgrade Steps for Projects Leveraging Data Lineage

#### DataLineage and ModelLineage Event Customization
[REQUIRED]
We have made adjustments regarding customizing the lineage event so that we can customize the lineage event based on the event type. If you have overridden any of the functions in the table below, they will need to be updated to their type-specific variants that are outlined in the _What Gets Generated_ section of the [Lineage documentation](https://boozallen.github.io/aissemble/aissemble/current/lineage-medatada-capture-overview.html#_what_gets_generated).

| Python Method Signature                                            | Java Method Signature                                    |
| ------------------------------------------------------------------ | -------------------------------------------------------- |
| create_run(self) → Run                                             | Run createRun()                                          |
| create_job(self) → Job                                             | Job createJob()                                          |
| create_run_event(self, run: Run, job: Job, status: str) → RunEvent | RunEvent createRunEvent(Run run, Job job, String status) |

#### Updated Namespace Conventions with Data Lineage
In order to follow standards for defining namespaces for OpenLineage Jobs and Datasets, the default behavior around namespace values has been changed.  The namespace values should be configured in the `data-lineage.properties` file. If your project does not already have this file, it will be automatically generated the next time you build. The following namespace configuration changes should be made.

[REQUIRED]
If your pipeline leverages any lineage Datasets, you must define a namespace for each dataset, per the configuration guidance in the [Lineage documentation](https://boozallen.github.io/aissemble/current-dev/lineage-medatada-capture-overview.html#_configuration):
```text
data.lineage.<dataset-name>.namespace=<dataset's-source-name>
```

[OPTIONAL]
If you are already setting the `data.lineage.namespace` value in your `<project-name>-docker/<project-name>-spark-worker-docker/src/main/resources/krausening/base/data-lineage.properties` file, it is recommended that you follow the [configuration documentation](https://boozallen.github.io/aissemble/current-dev/lineage-medatada-capture-overview.html#_configuration) and set `data.lineage.<pipeline>.namespace` and `data.lineage.<pipeline>.<step>.namespace` instead.

#### Associate Step Lineage Events to Pipeline
[OPTIONAL]
The data lineage now supports pipeline level lineage run event, which provides the parent run facet for all the step level lineage events, and helps to preserve pipeline-step job hierarchy and to tie all the step level lineage events' job together. To leverage this functionality, modify the driver class of your pipeline to import `PipelineBase` and using this, record the start and end lineage events of the pipeline execution.

##### pyspark pipeline driver class
```diff
  from krausening.logging import LogManager
+ from first_process.generated.pipeline.pipeline_base import PipelineBase

  if __name__ == "__main__":
      logger.info("STARTED: FirstProcess driver")
+     PipelineBase().record_pipeline_lineage_start_event()
      FirstStep().execute_step()
      ...
      LastStep().execute_step()
+     PipelineBase().record_pipeline_lineage_complete_event()
```

##### spark pipeline driver class
```diff
+ import com.boozallen.pipeline.PipelineBase;
  import org.slf4j.Logger;
  ...

  public static void main(String[] args) {
    logger.info("STARTED: {} driver", "SparkPipeline");
    SparkPipelineBaseDriver.main(args);

+   PipelineBase.getInstance().recordPipelineLineageStartEvent();
    ...
    final Step2 step2 = CDI.current().select(Step2.class, new Any.Literal()).get();
    CompletionStage<Void> step2Result = step2.executeStep();
    ...
+   PipelineBase.getInstance().recordPipelineLineageCompleteEvent();
```

## Final Steps - Required for All Projects

### Finalizing the Upgrade
1. Run `./mvnw org.technologybrewery.baton:baton-maven-plugin:baton-migrate -DoldHelmRepositoryUrl=<old-helm-repo>` to apply the automatic migrations
1. Run `./mvnw clean install` and resolve any manual actions that are suggested
    - **NOTE:** This will update any aiSSEMBLE dependencies in 'pyproject.toml' files automatically
2. Repeat the previous step until all manual actions are resolved

# What's Changed
