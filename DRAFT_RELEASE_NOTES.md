# Major Additions

## Helmfile Integration
We are removing the aiSSEMBLE provided automation for the path to production tool Tilt and ArgoCD. Having the local and higher environment deployment methods not aligned can cause hard-to-diagnose bugs and slow development. In an effort to have one tool to deploy to all environments, going forward aiSSEMBLE will support [Helmfile](https://helmfile.readthedocs.io/en/latest/) instead of Tilt and ArgoCD. New project will be generated with Helmfile and existing projects are encouraged to use it but are not required to. Follow **Finalizing the Upgrade** section for migration instructions.

## Reduced Spark Pipeline Size
We have pulled the Spark, Hadoop and Hive dependencies out of the shaded pipeline jar since they are already provided by Spark. This change can reduce the spark worker Docker image size and help resolve future CVEs faster.

## Sedona upgraded to 1.7.1
Previously, Sedona jars were on version 1.4.0. However, there are compatibility issues with this version of Sedona and Spark 3.5. The latest version also comes with a change to the names of the Sedona dependencies. It's recommended to replace all `sedona-*` dependencies with `sedona-spark-shaded-3.5_2.12`, which now includes all Sedona libraries. Accordingly, the geotools wrapper has also been updated from 1.4.0-28.2 to 1.7.1-28.5. See the [Sedona docs](https://sedona.apache.org/1.7.1/setup/maven-coordinates/) for more details.


## Spark Upgraded to 3.5.5 
Spark upgraded from 3.5.4 to 3.5.5. For upgrade information, see the Spark [release notes](https://spark.apache.org/news/spark-3-5-5-released.html).

# Breaking Changes
_Note: instructions for adapting to these changes are outlined in the upgrade instructions below._

- The data encryption modules marked for deletion in 1.13.0 have been removed. Follow **How to Upgrade** section for migration instructions. In addition, the following artifacts were removed:
  - extensions-encryption-vault-java java data encryption
  - foundation-encryption-policy-java java encryption policy
  - aissemble-extensions-encryption-vault-python python data encryption
  - aissemble-foundation-encryption-policy-python python encryption policy
- The default behavior on the `aissemble-infrastructure-chart` has been changed. The ArgoCD chart will no longer be deployed by default. To enable the ArgoCD deployment, follow the **How to Upgrade** section for details.
- Removing the support for local deployment tool,Tilt, and the ArgoCD

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

| Migration Name                                     | Description                                                                                                                                                                  |
|----------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| upgrade-tiltfile-aissemble-version-migration       | Updates the aiSSEMBLE version within your project's Tiltfile                                                                                                                 |
| upgrade-v2-chart-files-aissemble-version-migration | Updates the Helm chart dependencies within your project's deployment resources (`<YOUR_PROJECT>-deploy/src/main/resources/apps/`) to use the latest version of the aiSSEMBLE |
| upgrade-v1-chart-files-aissemble-version-migration | Updates the docker image tags within your project's deployment resources (`<YOUR_PROJECT>-deploy/src/main/resources/apps/`) to use the latest version of the aiSSEMBLE       |
| data-encryption-removal-pom-migration              | Remove the data encryption dependencies from the pom file                                                                                                                    |
| data-encryption-removal-pyproject-migration        | Remove the data encryption dependencies from the pyproject.toml file                                                                                                         |
| helmfile-generation-migration                      | Generates an initial helmfile.yaml for manual actions to be added to. After its ran once, it will automatically be added to the deactivated list                             |
| helmfile-aissemble-version-migration               | Updates the aiSSEMBLE version in the deployment values file. This version is used by the helmfile.yaml                                                                       |
| spark-provided-dependency-migration                | Remove the Hadoop, Hive, and Spark dependencies from the pipeline shaded jar                                                                                                 |

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
To start your aiSSEMBLE upgrade, update your project's pom.xml to use the 1.13.0 version of the build-parent:
```xml
<parent>
    <groupId>com.boozallen.aissemble</groupId>
    <artifactId>build-parent</artifactId>
    <version>1.13.0</version>
</parent>
```

## Conditional Steps

## For projects leveraging Data Encryption
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

## For projects leveraging the Configuration Store
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

## For projects leveraging the ArgoCD chart
With disabling the ArgoCD chart deployment configuration in the `aissemble-infrastructure-chart` by default, if you are using argocd locally, you will need to add the `argo-cd.enabled` configuration to your local values.yaml file as following:
```yaml
aissemble-infrastructure-chart:
  argo-cd:
+   enable: true

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
