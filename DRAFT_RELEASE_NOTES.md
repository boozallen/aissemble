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

## Dependency Upgrades
| Dependency                          | from          | to                                                   | Reason/Note                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
|-------------------------------------|---------------|------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Sedona                              | 1.4.0         | 1.7.1                                                | Previously, Sedona jars were on version 1.4.0. However, there are compatibility issues with this version of Sedona and Spark 3.5. The latest version also comes with a change to the names of the Sedona dependencies. It's recommended to replace all `sedona-*` dependencies with `sedona-spark-shaded-3.5_2.12`, which now includes all Sedona libraries. Accordingly, the geotools wrapper has also been updated from 1.4.0-28.2 to 1.7.1-28.5. See the [Sedona docs](https://sedona.apache.org/1.7.1/setup/maven-coordinates/) for more details. |
| Spark                               | 3.5.4         | 3.5.5                                                | For upgrade information, see the Spark [release notes](https://spark.apache.org/news/spark-3-5-5-released.html).                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| io.netty:netty-handler              | 4.1.111.Final | 4.1.118.Final                                        | Resolve Critical/High CVE issue                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| org.keycloak:keycloak-core          | 24.0.4        | 26.0.6                                               | Resolve Critical/High CVE issue                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| mysql:mysql-connector-java          | 8.0.30        | com.mysql:mysql-connector-j:9.2.0                    | Resolve Critical/High CVE issue                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| io.quarkus:quarkus-resteasy         | 3.8.6         | 3.8.6.1                                              | Resolve Critical/High CVE issue                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| com.thoughtworks.xstream:xstream    | 1.4.20        | 1.4.21                                               | Resolve Critical/High CVE issue                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| org.postgresql:postgresql           | 42.5.1        | 42.5.5                                               | Resolve Critical/High CVE issue                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| mlflow                              | 2.10.2        | 2.10.2                                               | Resolve Critical/High CVE issue                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| org.apache.velocity:velocity-tools  | 2.0           | org.apache.velocity.tools:velocity-tools-generic:3.1 | Resolve Moderate CVE issue                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| org.apache.poi:poi-ooxml            | 5.2.3         | 5.4.1                                                | Resolve Moderate CVE issue                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | 
| org.apache.kafka:kafka-clients      | 3.7.0         | 3.8.0                                                | Resolve Moderate CVE issue                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| org.codehaus.plexus:plexus-archiver | 4.8.0         | 4.10.0                                               | Resolve Moderate CVE issues                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| io.fabric8:kubernetes-client        | 6.13.4        | 7.1.0                                                | Resolve Moderate CVE issues                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| org.technologybrewery.habushu    | 2.18.1        | 3.0.0                             | For upgrade information, see the Habushu [release notes](https://github.com/TechnologyBrewery/habushu/releases)


# Breaking Changes
_Note: instructions for adapting to these changes are outlined in the upgrade instructions below._

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

# Known Issues

## Docker Module Build Failures
When using a Docker daemon that does not reside in `/var/run` (e.g. running Rancher Desktop without admin privileges) the docker-maven-plugin will fail to build with the message below. To work around this failure, set the `DOCKER_HOST` variable to the location of the daemon socket file. For example, to make the docker-maven-plugin work with Rancher Desktop, run `export DOCKER_HOST=unix://$HOME/.rd/docker.sock`.

```shell
[ERROR] Failed to execute goal org.technologybrewery.fabric8:docker-maven-plugin:0.45-tb-0.1.0:build (default-build) on project final-513-spark-worker-docker:
   Execution default-build of goal org.technologybrewery.fabric8:docker-maven-plugin:0.45-tb-0.1.0:build failed: 
   No <dockerHost> given, no DOCKER_HOST environment variable, no read/writable '/var/run/docker.sock' or '//./pipe/docker_engine' and no external provider like Docker machine configured
```

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

| Migration Name                                     | Description                                                                                                                                                                  | Argument                            |
|----------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------|
| upgrade-v2-chart-files-aissemble-version-migration | Updates the Helm chart dependencies within your project's deployment resources (`<YOUR_PROJECT>-deploy/src/main/resources/apps/`) to use the latest version of the aiSSEMBLE |                                     |
| upgrade-v1-chart-files-aissemble-version-migration | Updates the docker image tags within your project's deployment resources (`<YOUR_PROJECT>-deploy/src/main/resources/apps/`) to use the latest version of the aiSSEMBLE       |                                     |
| helmfile-aissemble-version-migration               | Updates the aiSSEMBLE version in the deployment values file. This version is used by the helmfile.yaml.gotmpl                                                                       |                                     |
| data-encryption-removal-pom-migration              | Remove the data encryption dependencies from the pom file                                                                                                                    |                                     |
| data-encryption-removal-pyproject-migration        | Remove the data encryption dependencies from the pyproject.toml file                                                                                                         |                                     |
| fastapi-removal-yaml-migration                     | Unwrap aissemble-fastapi-chart values in Helm chart values files files                                                                                                     |                                     |
| argocd-removal-migration                           | Remove the ArgoCD related application templates and values, Chart yaml files and etc.                                                                                        | aissemble.enable.helmfile.migration |
| helmfile-generation-migration                      | Generates an initial helmfile.yaml.gotmpl and helmfile-apps.yaml.gotmpl for manual actions to be added to                                                                                                         | aissemble.enable.helmfile.migration |
| helmfile-deployment-script-migration               | Updated JenkinsfileDeploy.groovy to use helmfile. Also removes the jenkinsPipelineSteps.groovy file                                                                          | aissemble.enable.helmfile.migration |
| helm-templates-migration                           | Move the non-argocd yaml files from the *-deploy/templates (root deploy) folder into the apps/common-infrastructure/templates directory                                      | aissemble.enable.helmfile.migration |
| helm-root-chart-migration                          | Move the root Chart.yaml and values.yaml files into the apps/common-infrastructure directory                                                                                 | aissemble.enable.helmfile.migration |
| spark-provided-dependency-migration                | Remove the Hadoop, Hive, and Spark dependencies from the pipeline shaded jar                                                                                                 |                                     |
| maven-build-cache-migration                        | Updates build cache configuration to avoid stale Docker images                                                                                                               |                                     |
| my-sql-connector-yaml-migration                    | Update the my-sql-connector package to use the version with no CVE issue                                                                                                     |                                     |
| trino-delta-lake-connector-yaml-migration          | Add the Delta Lake connector configuration in the Trino chart values.yaml file                                                                                               |                                     |
| habushu-monorepo-dependency-migration              | Ensures that all dependencies from Habushu modules on other Habushu modules in the same project are type `habushu`                                                           |                                     |

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

## Precondition Steps - Required for All Projects

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

## Final Steps - Required for All Projects
### Finalizing the Upgrade
1. Run `./mvnw org.technologybrewery.baton:baton-maven-plugin:baton-migrate` to apply the automatic migrations
    - **Note:** To enable the helmfile generation and ArgoCD removal migration, include the `aissemble.enable.helmfile.migration` property key when run the migration script, e.g.: `./mvnw org.technologybrewery.baton:baton-maven-plugin:baton-migrate -Daissemble.enable.helmfile.migration`
2. Run `./mvnw clean install` and resolve any manual actions that are suggested
    - **NOTE:** This will update any aiSSEMBLE dependencies in 'pyproject.toml' files automatically
3. Repeat the previous step until all manual actions are resolved

# What's Changed
_to be auto-generated when published_
