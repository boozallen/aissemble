# Major Additions

## Path to Production Alignment
To better align development processes with processes in CI/CD and higher environments, we no longer recommend using Tilt live-reloading.  As such, upgrading projects should consider narrowing the scope of their Tiltfile. See _**How to Upgrade**_ for more information.

## Data Access Upgrade
Data access through [GraphQL](https://graphql.org/) has been deprecated and replaced with [Trino](https://trino.io/). Trino is optimized for performing queries against large datasets by leveraging a distributed architecture that processes queries in parallel, enabling fast and scalable data retrieval.

# Breaking Changes
_Note: instructions for adapting to these changes are outlined in the upgrade instructions below._

 - The following Java classes have been renamed:
   | Old Java Class                | New Java Class                     |
   |-------------------------------|------------------------------------|
   | `AIOpsModelInstanceRepostory` | `AissembleModelInstanceRepository` |
   | `AiopsMdaJsonUtils`           | `AissembleMdaJsonUtils`            |
 - To improve the development cycle and docker build consistency, we have deprecated the docker_build() and local_resources() functions in the Tilt and enable maven docker build for the docker modules. Follow the instruction in the `Finalizing the Upgrade` to avoid duplicated docker image build.


# Known Issues
_There are no known issues with the 1.11 release._

# Known Vulnerabilities

| Date<br/>identified | Vulnerability | Severity | Package | Affected <br/>versions | CVE | Fixed <br/>in |
|---------------------|---------------|----------|---------|------------------------|-----|---------------|


# How to Upgrade

The following steps will upgrade your project to `1.11`. These instructions consist of multiple phases:
- Automatic Upgrades - no manual action required
- Precondition Steps - needed in all situations
- Conditional Steps (e.g., Python steps, Java steps, if you use Metadata, etc)
- Final Steps - needed in all situations

## Automatic Upgrades
To reduce burden of upgrading aiSSEMBLE, the Baton project is used to automate the migration of some files to the new version.  These migrations run automatically when you build your project, and are included by default when you update the `build-parent` version in your root POM.  Below is a description of all of the Baton migrations that are included with this version of aiSSEMBLE.

| Migration Name                                       | Description                                                                                                                                                                             |
|------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| upgrade-tiltfile-aissemble-version-migration         | Updates the aiSSEMBLE version within your project's Tiltfile                                                                                                                            |
| upgrade-v2-chart-files-aissemble-version-migration   | Updates the Helm chart dependencies within your project's deployment resources (`<YOUR_PROJECT>-deploy/src/main/resources/apps/`) to use the latest version of the aiSSEMBLE            |
| upgrade-v1-chart-files-aissemble-version-migration   | Updates the docker image tags within your project's deployment resources (`<YOUR_PROJECT>-deploy/src/main/resources/apps/`) to use the latest version of the aiSSEMBLE                  |
| pipeline-invocation-service-template-migrtion        | Include the helm.valueFiles param to ArgoCD pipeline-invocation-service template                                                                                                        |                                                                                                                                                      
| docker-module-pom-dependency-type-migration          | Updates the maven pipeline dependency type within your project's sub docker module pom file(`<YOUR_PROJECT>-docker/*-docker/pom.xml`) to fix the build cache checksum calculation issue |
| enable-maven-docker-build-migration                  | Remove the maven fabric8 plugin `skip` configuration within your project's docker module pom file(`<YOUR_PROJECT>-docker/pom.xml`) to enable the maven docker build                     |
| spark-worker-docker-image-tag-migration              | Updated the worker docker image tag to use project version                                                                                                                              |

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
To start your aiSSEMBLE upgrade, update your project's pom.xml to use the 1.11.0 version of the build-parent:
```xml
<parent>
    <groupId>com.boozallen.aissemble</groupId>
    <artifactId>build-parent</artifactId>
    <version>1.11.0</version>
</parent>
```

### Tilt Docker Builds
To avoid duplicate docker builds, remove all the related `docker_build()` and `local_resources()` functions from your Tiltfile. Also, the `spark-worker-image.yaml` is no longer used so the `-deploy/src/main/resources/apps/spark-worker-image` directory and the related `k8s_yaml()` function from your Tiltfile can be removed.

## Conditional Steps

## Final Steps - Required for All Projects
### Finalizing the Upgrade
1. Run `./mvnw org.technologybrewery.baton:baton-maven-plugin:baton-migrate` to apply the automatic migrations
2. Run `./mvnw clean install` and resolve any manual actions that are suggested
    - **NOTE:** This will update any aiSSEMBLE dependencies in 'pyproject.toml' files automatically
3. Repeat the previous step until all manual actions are resolved

# What's Changed
_to be auto-generated when published_
