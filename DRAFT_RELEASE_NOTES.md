# Major Additions

## Java 17 Upgrade
The aiSSEMBLE project is now built with Java 17. All dependencies inherited from aiSSEMBLE have been updated to a Java 17 compatible version and automatic migrations (detailed below) have been created to streamline this transition for downstream projects. It is recommended to review non-inherited dependencies and custom logic within your project, as this upgrade can break existing functionality due to deprecated classes and incompatible dependencies.

## Python Version Supported Expanded
All aiSSEMBLE python libraries now support a minimum version of 3.8 (previously 3.11).

## Improved Licence Generation
Leveraging a new version of Booz Allen Licenses. Booz Allen projects may find some minor updates to header file text that better conforms to some strict linting standards.

# Breaking Changes
_Note: instructions for adapting to these changes are outlined in the upgrade instructions below._

- All projects must be built with Java 17. The aiSSEMBLE team recommends [SDKMan](https://sdkman.io/) for managing multiple Java versions.
- The new minimum required Maven version is now `3.9.6` to ensure compatibility with Java 17. The aiSSEMBLE team recommens using the [Maven Wrapper](https://maven.apache.org/wrapper/) to ensure compatibility.
- Deprecated features were removed:
  - All SageMaker modules and corresponding references were removed in commit 8ce393f.
  - The Service Discovery module and corresponding references were removed in commit d10db5d0.
  - The `data.lineage.namespace` legacy property was removed. For more guidance, please refer to the [Lineage Metadata GitHub documentation](https://boozallen.github.io/aissemble/aissemble/current-dev/lineage-medatada-capture-overview.html#_configuration).


# Known Issues
_There are no known issues with the 1.10 release._

# Known Vulnerabilities
| Date<br/>identified | Vulnerability                                                             | Severity | Package                   | Affected <br/>versions | CVE                                                               | Fixed <br/>in |
|---------------------|---------------------------------------------------------------------------|----------|---------------------------|------------------------|-------------------------------------------------------------------|---------------|


# How to Upgrade

>[!WARNING]
> The upgrade process is a little different for this release to support the Java 17 updates. Specifically, the finalization
> section has a few more steps.

The following steps will upgrade your project to `1.10`. These instructions consist of multiple phases:
- Automatic Upgrades - no manual action required
- Precondition Steps - needed in all situations
- Conditional Steps (e.g., Python steps, Java steps, if you use Metadata, etc)
- Final Steps - needed in all situations

## Automatic Upgrades
To reduce burden of upgrading aiSSEMBLE, the Baton project is used to automate the migration of some files to the new version.  These migrations run automatically when you build your project, and are included by default when you update the `build-parent` version in your root POM.  Below is a description of all of the Baton migrations that are included with this version of aiSSEMBLE.

| Migration Name                                     | Description                                                                                                                                                                                                                                  |
|----------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| upgrade-tiltfile-aissemble-version-migration       | Updates the aiSSEMBLE version within your project's Tiltfile                                                                                                                                                                                 |
| upgrade-v2-chart-files-aissemble-version-migration | Updates the Helm chart dependencies within your project's deployment resources (`<YOUR_PROJECT>-deploy/src/main/resources/apps/`) to use the latest version of the aiSSEMBLE                                                                 |
| upgrade-v1-chart-files-aissemble-version-migration | Updates the docker image tags within your project's deployment resources (`<YOUR_PROJECT>-deploy/src/main/resources/apps/`) to use the latest version of the aiSSEMBLE                                                                       |
| spark-version-upgrade-migration                    | Updates the Spark Application executor failure parameters to their new key name to ensure compatibility with Apache Spark `3.5`                                                                                                              |
| spark-pipeline-messaging-pom-migration             | Updates a Spark pipeline module `pom.xml` with the new CDI classes dependency to ensure messaging compatibility with Java 17                                                                                                                 |
| spark-pipeline-messaging-cdi-factory-migration     | Updates a Spark pipeline module `CdiContainerFactory.java` with the new CDI classes to ensure messaging compatibility with Java 17                                                                                                           |
| spark-pipeline-servlet-api-migration               | Updates a Spark pipeline module `pom.xml` with the `javax.servlet-api` dependency to ensure compatibility with Apache Spark `3.5` which has not migrated to Jakarta packages yet                                                             |
| it-infrastructure-java-upgrade-migration           | Updates the Java docker image version in the integration test docker module to JDK 17                                                                                                                                                        |
| log4j-maven-shade-plugin-migration                 | Updates the Maven Shade Plugin with the new Log4j dependency information                                                                                                                                                                     |
| quarkus-bom-migration                              | Updates all references to the `quarkus-bom` and `quarkus-universe-bom` to use the new `aissemble-quarkus-bom` for managing Quarkus dependencies                                                                                              |
| pom-dependency-version-migration                   | Updates the pom dependencies previously managed by the aiSSEMBLE `bom-component` to include their necessary versions                                                                                                                         |
| java-package-migration                             | Updates the affected java classes from their old package name to their new package name to ensure compatibility with the updated Java 17 dependencies. This migration primarily updates `javax.*` packages to their new `jakarta.*` packages |
| alerting-cdi-migration                             | Adds `AlertingCdiContext` to `CdiContainerFactory.getContexts` for data delivery pipelines that depend on `foundation-alerting`                                                                                                              |

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
To start your aiSSEMBLE upgrade, update your project's pom.xml to use the 1.10.0 version of the build-parent:
```xml
<parent>
    <groupId>com.boozallen.aissemble</groupId>
    <artifactId>build-parent</artifactId>
    <version>1.10.0</version>
</parent>
```

### BOM Component Replacement
All references to the `com.boozallen.aissemble:bom-component` should be replaced with the new `com.boozallen.aissemble:aissemble-quarkus-bom`:
```diff
    <dependency>
        <groupId>com.boozallen.aissemble</groupId>
-        <artifactId>bom-component</artifactId>
+        <artifactId>aissemble-quarkus-bom</artifactId>
        <version>${version.aissemble}</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
```

## Conditional Steps

### For projects created before version 1.7.0
Older projects were not generated with a [Maven Wrapper](https://maven.apache.org/wrapper/) configuration by default. To ease Maven version management and ensure consistent builds across environments, the aiSSEMBLE team recommends using a wrapper. Simply create a `.mvn/wrapper` directory and place a aiSSEMBLE-compatible [maven-wrapper.properties](https://github.com/boozallen/aissemble/blob/main/.mvn/wrapper/maven-wrapper.properties) inside (see below). Then run `mvn wrapper:wrapper` to generate the two `mvnw` executables. This Maven wrapper should be used over the system-wide Maven for all operations.

```properties
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip
wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar
```

### For projects that have customized the Spark Operator Service Account permissions
The service account for the pipeline invocation service is now separated from spark operator and configured solely for the service.
If you added any custom configurations to the `sparkoperator` service account pertaining to the pipeline invocation service, you will need to migrate the related changes to the new `pipeline-invocation-service-sa`. Refer to Pipeline Invocation Helm Chart [README](https://github.com/boozallen/aissemble/blob/dev/extensions/extensions-helm/extensions-helm-pipeline-invocation/aissemble-pipeline-invocation-app-chart/README.md) for detail.

## Final Steps - Required for All Projects
### Finalizing the Upgrade
1. Ensure you are using Java 17 for Maven. (Run `mvn -v` to check.)
2. Run `./mvnw org.technologybrewery.baton:baton-maven-plugin:baton-migrate` to apply the automatic migrations
3. Remove the `com.boozallen.aissemble:aissemble-quarkus-bom:${version.aissemble}` dependency from the root `pom.xml` and `<YOUR_PROJECT>-tests/<YOUR_PROJECT>-tests-java/pom.xml` of your project
    - **NOTE:** Any Quarkus apps within your project should be updated to include the `com.boozallen.aissemble:aissemble-quarkus-bom:${version.aissemble}` within the `<dependencyManagement>` section of their respective `pom.xml` instead
4. Run `./mvnw clean install` and resolve any manual actions that are suggested
    - **NOTE:** This will update any aiSSEMBLE dependencies in 'pyproject.toml' files automatically
5. Repeat the previous step until all manual actions are resolved

# What's Changed

