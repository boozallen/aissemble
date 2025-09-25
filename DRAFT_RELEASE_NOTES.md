# Major Additions

## Helmfile Integration
Given that the local and higher environment deployment methods not aligned can cause hard-to-diagnose bugs and slow development, in an effort to have one tool to deploy to all environments, going forward aiSSEMBLE will support [Helmfile](https://helmfile.readthedocs.io/en/latest/) instead of Tilt and ArgoCD. New project will be generated with Helmfile and existing projects are encouraged to use it but are not required to. Follow **Finalizing the Upgrade** section for migration instructions.

## Removal of Integration Tests
New projects generated on version 1.13.0 of aiSSEMBLE will no longer include an automatically generated `<project-name>-tests` module. If you upgrade an existing project to 1.13.0, your `<project-name>-tests` module will remain intact — but be aware that there will not be any further updates or enhancements to these integration tests going forward.

In addition, the following Fermenter profiles related to the integration tests module have been deprecated and will be removed in version 1.14.0 of aiSSEMBLE:
- `integration-test-docker`
- `integration-test-chart`
- `integration-test-data-pipeline`

## Spark BOM and Reduced Spark Pipeline Size
To help ensure data delivery pipelines are compatible with the `aissemble-spark` image, and to exclude jars provided by the image from shaded pipeline jars, we have created the `aissemble-spark-bom`. This BOM ensures Spark, Hadoop and Hive dependencies are not included in the shaded pipeline jar significantly reducing the spark worker Docker image size and ensures pipelines are unit tested against the same versions of libraries as are on the `aissemble-spark` image, including patches for CVE resolution.

Spark upgraded from 3.5.4 to 3.5.5. For upgrade information, see the Spark [release notes](https://spark.apache.org/news/spark-3-5-5-released.html).

## Poetry 2 Support and Minimum Python
New projects generated on version 1.13.0 of aiSSEMBLE will use Poetry 2.x.  With this update, the minimum version of Python supported by aiSSEMBLE is updated from 3.8 to 3.9.

## Habushu 3.0 upgrade
Habushu has been upgraded to [version 3.0](https://github.com/TechnologyBrewery/habushu/releases/tag/habushu-3.0.0). Because Habushu 3 has dropped support for other containerization approaches, aiSSEMBLE will now use the [containerize-dependencies feature](https://github.com/TechnologyBrewery/habushu/blob/dev/docs/CONFIGURATION_README.md#containerization-configurations) to package PySpark and Machine Learning pipelines.  The Habushu project has [examples](https://github.com/TechnologyBrewery/habushu/blob/dev/examples/poetry/habushu-poetry-containerize/README.md) to help illustrate how containerization works.

## Dependency Upgrades
| Language | Dependency                                           | from          | to           | Reason/Note                                                                                                                                                                                                                                               |
|----------|------------------------------------------------------|---------------|--------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Platform | Spark                                                | 3.5.4         | 3.5.5        | For upgrade information, see the Spark [release notes](https://spark.apache.org/news/spark-3-5-5-released.html).                                                                                                                                          |
| Java     | cucumber-reporting (net.masterthought)               | 5.8.1         | 5.9.0        | Resolve vulnerabilities                                                                                                                                                                                                                                   |
| Java     | elasticsearch-spark-30_2.12 (org.elasticsearch)      | 8.9.0         | 9.1.2        | Resolve vulnerabilities                                                                                                                                                                                                                                   |
| Java     | geotools-wrapper (org.datasyslab)                    | 1.4.0-28.2    | 1.7.1-28.5   | Updates to match new Sedona version                                                                                                                                                                                                                       |
| Java     | jclouds-* (org.apache.jclouds)                       | 2.6.0         | 2.7.0        | Resolve vulnerabilities                                                                                                                                                                                                                                   |
| Java     | jjwt-* (io.jsonwebtoken)                             | 0.11.2        | 0.12.7       | Resolve vulnerabilities. Breaking changes - see [0.12 release notes](https://github.com/jwtk/jjwt/blob/0.12.0/CHANGELOG.md)                                                                                                                               |
| Java     | kafka-clients (org.apache.kafka)                     | 3.7.0         | 3.9.1        | Resolve vulnerabilities                                                                                                                                                                                                                                   |
| Java     | keycloak-core (org.keycloak)                         | 24.0.4        | 26.3.2       | Resolve vulnerabilities                                                                                                                                                                                                                                   |
| Java     | kubernetes-client (io.fabric8)                       | 6.13.4        | 7.1.0        | Resolve vulnerabilities                                                                                                                                                                                                                                   |
| Java     | mysql-connector-java (mysql)                         | 8.0.30        | 9.2.0        | Resolve vulnerabilities. Renamed to mysql-connector-j.                                                                                                                                                                                                    |
| Java     | netty-handler (io.netty)                             | 4.1.111.Final | 4.1.24.Final | Resolve vulnerabilities                                                                                                                                                                                                                                   |
| Java     | openlineage-java (io.openlineage)                    | 1.23.0        | 1.30.0       | Resolve vulnerabilities. To prepare for future breaking changes, we recommend using the builder API, or the aiSSEMBLE classes only.                                                                                                                       |
| Java     | plexus-archiver (org.codehaus.plexus)                | 4.8.0         | 4.10.0       | Resolve vulnerabilities                                                                                                                                                                                                                                   |
| Java     | poi-ooxml (org.apache.poi)                           | 5.2.3         | 5.4.1        | Resolve vulnerabilities                                                                                                                                                                                                                                   | 
| Java     | postgresql (org.postgresql)                          | 42.5.1        | 42.5.5       | Resolve vulnerabilities                                                                                                                                                                                                                                   |
| Java     | quarkus-* (io.quarkus)                               | 3.8.6         | 3.15.6       | Resolve vulnerabilities                                                                                                                                                                                                                                   |
| Java     | resteasy-* (org.jboss.resteasy)                      | 6.2.8.Final   | 6.2.12.Final | Resolve vulnerabilities                                                                                                                                                                                                                                   |
| Java     | sedona-* (org.apache.sedona)                         | 1.4.0         | 1.7.1        | Upgraded due to compatibility issues with Spark 3.5. Because of artifact renames, we recommend replacing all `sedona-*` dependencies with `sedona-spark-shaded-3.5_2.12`. See the [Sedona docs](https://sedona.apache.org/1.7.1/setup/maven-coordinates/) |
| Java     | smallrye-reactive-messaging-* (io.smallrye.reactive) | 4.24.0        | 4.28.0       | Resolve vulnerabilities                                                                                                                                                                                                                                   |
| Java     | vertx-web-client (io.vertx)                          | 4.5.9         | 4.5.16       | Resolve vulnerabilities                                                                                                                                                                                                                                   |
| Java     | xstream (com.thoughtworks.xstream)                   | 1.4.20        | 1.4.21       | Resolve vulnerabilities                                                                                                                                                                                                                                   |
| Maven    | habushu-maven-plugin                                 | 2.18.1        | 3.0.0        | For upgrade information, see the Habushu [release notes](https://github.com/TechnologyBrewery/habushu/releases)                                                                                                                                           |
| Python   | mlflow                                               | 2.3.1         | 2.22.0       | Resolve vulnerabilities                                                                                                                                                                                                                                   |
| Python   | pandas                                               | ^1.5.0        | >=1.5.0      | Fixes version incompatibility with MLFlow 2.22.0                                                                                                                                                                                                          |
| Python   | protobuf                                             | ^3.20.3       | 4.25.8       | Resolve vulnerabilities                                                                                                                                                                                                                                   |
| Python   | urllib3                                              | ^1.26.18      | >=2.5.0      | Resolve vulnerabilities                                                                                                                                                                                                                                   |

# Breaking Changes
_Note: instructions for adapting to these changes are outlined in the upgrade instructions below._

- Release settings are no longer inherited from the baseline
- The data encryption modules marked for deletion in 1.13.0 have been removed. Follow **How to Upgrade** section for migration instructions. In addition, the following artifacts were removed:
  - extensions-encryption-vault-java java data encryption
  - foundation-encryption-policy-java java encryption policy
  - aissemble-extensions-encryption-vault-python python data encryption
  - aissemble-foundation-encryption-policy-python python encryption policy
- The following aissemble-fastapi artifacts marked for deletion in 1.13.0 have been removed:
  - `aissemble-fastapi-chart` helm chart
  - `aissemble-fastapi` docker image
- The default behavior on the `aissemble-infrastructure-chart` has been changed. The ArgoCD chart will no longer be deployed by default. To enable the ArgoCD deployment, follow the **How to Upgrade** section for details.
- Removed local deployment tool Tilt, and the remote deployment tool ArgoCD as we migrated to use Helmfile as the aiSSEMBLE deployment tool.
- Habushu is now configured with [`usePyenv`](https://github.com/TechnologyBrewery/habushu/tree/3b885791b347de91b02124633b32b2b723cdd6e2/docs#usepyenv) set to `false` by default in the `ci` profile
- Habushu has been updated to version `3.0.0`. Review the [Habushu 3.0.0 Release Notes](https://github.com/TechnologyBrewery/habushu/releases/tag/habushu-3.0.0) for details on breaking changes and instructions on how to address them.
- Bitnami has made changes to its public bitnami catalog (docker.io/bitnami), it moves Debian-based images to the [Bitnami Legacy repository](https://hub.docker.com/u/bitnamilegacy). We have updated our related images to use the bitnami legacy repository accordingly.

# Known Issues
_There are no known issues with the 1.13 release._

# Known Vulnerabilities

| Date<br/>identified | Vulnerability | Severity | Package | Affected <br/>versions | CVE | Fixed <br/>in |
|---------------------|---------------|----------|---------|------------------------|-----|---------------|

# How to Upgrade

The following steps will upgrade your project to `1.13`. These instructions consist of multiple phases:
- Automatic Upgrades - no manual action required
- Precondition Steps - needed in all situations
- Conditional Steps (e.g., Python steps, Java steps, if you use Metadata, etc)
- Final Steps - needed in all situations

## Automatic Upgrades
To reduce burden of upgrading aiSSEMBLE, the Baton project is used to automate the migration of some files to the new version.  These migrations run automatically when you build your project, and are included by default when you update the `build-parent` version in your root POM.  Below is a description of all of the Baton migrations that are included with this version of aiSSEMBLE.

| Migration Name                                     | Description                                                                                                                                                                                                                                          | Argument                            |
|----------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------|
| upgrade-v2-chart-files-aissemble-version-migration | Updates the Helm chart dependencies within your project's deployment resources (`<YOUR_PROJECT>-deploy/src/main/resources/apps/`) to use the latest version of the aiSSEMBLE                                                                         |                                     |
| upgrade-v1-chart-files-aissemble-version-migration | Updates the docker image tags within your project's deployment resources (`<YOUR_PROJECT>-deploy/src/main/resources/apps/`) to use the latest version of the aiSSEMBLE                                                                               |                                     |
| helmfile-aissemble-version-migration               | Updates the aiSSEMBLE version in the deployment values file. This version is used by the helmfile.yaml.gotmpl                                                                                                                                        |                                     |
| data-encryption-removal-pom-migration              | Remove the data encryption dependencies from the pom file                                                                                                                                                                                            |                                     |
| data-encryption-removal-pyproject-migration        | Remove the data encryption dependencies from the pyproject.toml file                                                                                                                                                                                 |                                     |
| fastapi-removal-yaml-migration                     | Unwrap aissemble-fastapi-chart values in Helm chart values files files                                                                                                                                                                               |                                     |
| argocd-removal-migration                           | Remove the ArgoCD related application templates and values, Chart yaml files and etc.                                                                                                                                                                | aissemble.enable.helmfile.migration |
| helmfile-generation-migration                      | Generates an initial helmfile.yaml.gotmpl and helmfile-apps.yaml.gotmpl for manual actions to be added to                                                                                                                                            | aissemble.enable.helmfile.migration |
| helmfile-deployment-script-migration               | Updated JenkinsfileDeploy.groovy to use helmfile. Also removes the jenkinsPipelineSteps.groovy file                                                                                                                                                  | aissemble.enable.helmfile.migration |
| helm-templates-migration                           | Move the non-argocd yaml files from the *-deploy/templates (root deploy) folder into the apps/common-infrastructure/templates directory                                                                                                              | aissemble.enable.helmfile.migration |
| helm-root-chart-migration                          | Move the root Chart.yaml and values.yaml files into the apps/common-infrastructure directory                                                                                                                                                         | aissemble.enable.helmfile.migration |
| spark-provided-dependency-migration                | Remove the Hadoop, Hive, and Spark dependencies from the pipeline shaded jar                                                                                                                                                                         |                                     |
| maven-build-cache-migration                        | Updates build cache configuration to avoid stale Docker images                                                                                                                                                                                       |                                     |
| my-sql-connector-yaml-migration                    | Update the my-sql-connector package to use the version with no vulnerabilities                                                                                                                                                                             |                                     |
| trino-delta-lake-connector-yaml-migration          | Add the Delta Lake connector configuration in the Trino chart values.yaml file                                                                                                                                                                       |                                     |
| ruff-toml-file-generation-migration                | Generates an initial ruff.toml file with a few lines of configuration to make the ruff linting and formatting consistent with prior linting and formatting.  This file can be configured using https://docs.astral.sh/ruff/configuration/ as a guide |                                     |
| habushu-monorepo-dependency-migration              | Ensures that all dependencies from Habushu modules on other Habushu modules in the same project are type `habushu`                                                                                                                                   |                                     |
| inference-docker-pom-migration                     | Update ML inference docker image generation to use Habushu's containerize-dependencies goal by adding the goal to the maven POM file                                                                                                                 |                                     |
| spark-bom-dependency-migration                     | Adds the `aissemble-spark-bom` to data delivery pipelines for centralized Spark, Hadoop, and Hive dependency management and ensures version alignment with the `aissemble-spark` image                                                               |                                     |
| ml-train-pipeline-docker-migration                 | Adds Habushu `containerize-dependencies` goal to relevant ML training pipeline docker pom files                                                                                                                                                      |                                     |
| root-dependency-version-migration                  | Updates the hard coded aissemble versions within the projects root POM file dependencies                                                                                                                                                             |                                     |

Migrations with arguments will not be executed unless that argument is provided (e.g. `./mvnw org.technologybrewery.baton:baton-maven-plugin:baton-migrate -D<argumentName>`). To deactivate any of these migrations, add the following configuration to the `baton-maven-plugin` within your root `pom.xml`:

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

## Precondition Steps

### For projects using the maven-release-plugin
Prior to upgrading, generate an effective POM with the `help` plugin and ensure all necessary configurations for the 
`maven-release-plugin` and `nexus-staging-maven-plugin` are present directly in your project. After upgrading, the
configuration values inherited from the baseline will no longer be present.
```shell
mvn help:effective-pom -Doutput=effective-pom.xml
```

### Beginning the Upgrade
To start your aiSSEMBLE upgrade, update your project's pom.xml to use the 1.13.0 version of the build-parent:
```xml
<parent>
    <groupId>com.boozallen.aissemble</groupId>
    <artifactId>build-parent</artifactId>
    <version>1.13.0</version>
</parent>
```

## Conditional Steps

### For projects leveraging Data Encryption
With data encryption removal, we are no longer supporting below functions/policy in the project. If you are using any of these functions/policy, please make changes accordingly:

- Spark/Pyspark Pipeline
  - encryptUDF
  - checkAndApplyEncryptionPolicy
  
- Pyspark Pipeline
  - aissemble_encrypt_simple_aes
  - aissemble_encrypt_with_vault_key
  - aissemble_encrypt_aes_udf
  - aissemble_encrypt_vault_udf
  - check_and_apply_encryption_policy
  - apply_encryption_when_native_collection_is_supplied
  - apply_encryption_to_dataset

- Spark/Pyspark Pipeline Dictionary Model
  - protectionPolicy

### For projects leveraging the Configuration Store
With data encryption removal, the encrypt.properties have been migrated to configuration store. If you
are using the configuration store with vault, please ensure to rename the `encrypt.properties` to be 
`config-store-vault.properties`. For the vault deployment, you can add below content to the vault 
values.yaml file to enable configuration store access vault:
```yaml
  aissemble-configuration-store-chart:
    configMapProperties:
      enabled: true
      name: configuration-store-config
      baseConfigFiles:
+       config-store-valut.properties: |-
+         secrets.host.url=http://127.0.0.1:8200
+         secrets.root.key=<key>
+         secrets.unseal.keys==key1,key2,key3
```

### For projects moving to Helmfile
Much of the migration to Helmfile can be automated with Baton migrations by including the command line flag `-Daissemble.enable.helmfile.migration` during the `baton-migrate` step of _Finalizing the Upgrade_.  To prepare local environments for using Helmfile, users will need to install the `helm-diff` plugin (alongside the [Helmfile](https://helmfile.readthedocs.io/en/latest/#installation)) with the following command:
```bash
helm plugin install https://github.com/databus23/helm-diff
```
As part of this update, the `helmfile-deployment-script-migration` will totally replace the `devope/JenkinsDeploy.groovy` content. Use git diff to restore any necessary customizations.

**Note:** To enable Helmfile migrations, include `-Daissemble.enable.helmfile.migration` i.e.:
   `./mvnw org.technologybrewery.baton:baton-maven-plugin:baton-migrate -Daissemble.enable.helmfile.migration`

### For projects wishing to retain Tilt/ArgoCD
Helmfile will be the default CI/CD tool going forward. aiSSEMBLE support for Tilt and ArgoCD has been deprecated. Teams choosing to retain Tilt or ArgoCD must now independently manage and maintain their respective configurations. 

With disabling the ArgoCD chart deployment configuration in the `aissemble-infrastructure-chart` by default, if you are using argocd locally, you will need to add the `argo-cd.enabled` configuration to your local values.yaml file as following:
```yaml
aissemble-infrastructure-chart:
  argo-cd:
+   enable: true

```

### For projects that deploy to a custom namespace
The `aissemble-spark-application` chart has been updated to default the namespace to the release namespace (e.g. provided by `helm install --namespace X`). Unless your project needs the namespace hard-coded, it is recommended to remove `metadata.namespace` from your pipeline's _*-base-values.yaml_ file.  For projects using a GitOps approach that _relies_ on a hard-coded namespace the `metadata.namespace` property will still take precedences over the release namespace.

### For projects that require using PyEnv in CI
It is recommended that you use Python directly in CI, especially when installing Python from scratch.  If you cannot use Python directly and require version switching (e.g. a
shared build environment with different version requirements between projects) you can switch the configuration of Habushu back to the previous setting by adding the following to
your root _pom.xml_:

```xml
    <profiles>
        <profile>
            <id>ci</id>
            <build>
                <pluginManagement>
                    <plugins>
                        <plugin>
                            <groupId>org.technologybrewery.habushu</groupId>
                            <artifactId>habushu-maven-plugin</artifactId>
                            <configuration>
                                <usePyenv>true</usePyenv>
                            </configuration>
                        </plugin>
                    </plugins>
                </pluginManagement>
            </build>
        </profile>
    </profiles>
```

### For projects using Python
With the Habushu version upgrade to 3.0.0, aiSSEMBLE will leverage Habushu's containerize-dependencies goal for these pipelines. Migrations are in place to automatically update the POM files for these docker modules but not the Dockerfile's themselves. Habushu will insert code in place of the tags `#HABUSHU_BUILDER_STAGE` and `#HABUSHU_FINAL_STAGE`. If they are not present, Habushu will place them at the top and bottom of the Dockerfile so you will need to add these tags into your dockerfile where it makes sense. Visit [Habushu's documentation](https://github.com/TechnologyBrewery/habushu/blob/dev/docs/HABUSHU_LIFECYCLE_README.md#containerize-dependencies) for more information. This is an example of a ML Inference and Ml Training dockerfile with the Habushu tags
```dockerfile
ARG DOCKER_BASELINE_REPO_ID
ARG VERSION_AISSEMBLE

#HABUSHU_BUILDER_STAGE

#HABUSHU_FINAL_STAGE

ENV GIT_PYTHON_REFRESH=quiet
ENV ENABLE_LINEAGE=true

# Ensure that any needed Krausening properties are copied and KRAUSENING_BASE environment variable is set!
ENV KRAUSENING_BASE /app/config
COPY target/krausening/base/ /app/config/
COPY src/main/resources/krausening/base/ /app/config/

# Start the drivers
CMD ...
```


Here is an example of a Spark worker Dockerfile with the Habushu tags:
```diff
...

- FROM ${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-spark:${VERSION_AISSEMBLE}
+ FROM ${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-spark:${VERSION_AISSEMBLE} AS root_habushu_builder
USER root

- WORKDIR /

- #Install 3rd party Pyspark pipeline dependencies (does nothing if you only have Spark pipelines)
- COPY ./target/dockerbuild/requirements/. /tmp/requirements/
- RUN set -e && files=$(find /tmp/requirements -path '/tmp/requirements/*/*' -name requirements.txt -type f); for file in $files; do python3 -m pip install --no-cache-dir -r $file || exit 1; done;

- #Install monorepo Pyspark pipeline dependencies (does nothing if you only have Spark pipelines)
- COPY ./target/dockerbuild/wheels/. /tmp/wheels/
- RUN set -e && files=$(find /tmp/wheels -path '/tmp/wheels/*' -name '*.whl' -type f); for file in $files; do python3 -m pip install --no-cache-dir $file || exit 1; done;

+ #HABUSHU_BUILDER_STAGE

+ #HABUSHU_FINAL_STAGE

+ RUN chown -R spark:spark /opt/venv

#Pipelines
COPY --chown=spark:spark --chmod=777 ./target/dockerbuild/. /opt/spark/jobs/pipelines/

- #Install Pyspark pipelines (does nothing if you only have Spark pipelines)
- RUN set -e && files=$(find /opt/spark/jobs -path '/opt/spark/jobs/pipelines/*/*' -name '*.tar.gz' -type f); for file in $files; do python3 -m pip install --no-deps --no-cache-dir $file || exit 1; done;

COPY --chown=spark ./src/main/resources/krausening/ ${SPARK_HOME}/krausening/

# Switch to the spark user (which *is* 1001)
USER spark
WORKDIR /opt/spark/work-dir/
```

## Final Steps - Required for All Projects
### Finalizing the Upgrade
1. Run `./mvnw org.technologybrewery.baton:baton-maven-plugin:baton-migrate` to apply the automatic migrations
    - **Note:** To enable the helmfile generation and ArgoCD removal migration, include the `aissemble.enable.helmfile.migration` property key when run the migration script, e.g.: `./mvnw org.technologybrewery.baton:baton-maven-plugin:baton-migrate -Daissemble.enable.helmfile.migration`
2. Run `./mvnw clean install` and resolve any manual actions that are suggested
    - **NOTE:** This will update any aiSSEMBLE dependencies in 'pyproject.toml' files automatically
3. Repeat the previous step until all manual actions are resolved

# What's Changed
_to be auto-generated when published_
