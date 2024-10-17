# Major Additions

## TBD

# Breaking Changes
- Note: instructions for adapting to these changes are outlined in the upgrade instructions below.

With the upgrade to Java 17, the new minimum required Maven version is now 3.9.6. This can result in deprecated classes or incompatible dependencies, please update your modules accordingly.


# Known Issues
_There are no known issues with the 1.10 release._

# Known Vulnerabilities
| Date<br/>identified | Vulnerability                                                             | Severity | Package                   | Affected <br/>versions | CVE                                                               | Fixed <br/>in |
|---------------------|---------------------------------------------------------------------------|----------|---------------------------|------------------------|-------------------------------------------------------------------|---------------|


# How to Upgrade
The following steps will upgrade your project to 1.10. These instructions consist of multiple phases:
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
| spark-version-upgrade-migration                    | Updates the Spark Application executor failure parameters to their new key name to ensure compatibility with spark `3.5`                                                     |
| spark-pipeline-messaging-pom-migration             | Updates a Spark pipeline module `pom.xml` with the new CDI classes dependency to ensure messaging compatibility with Java 17                                                             |
| spark-pipeline-messaging-cdi-factory-migration     | Updates a Spark pipeline module `CdiContainerFactory.java` with the new CDI classes to ensure messaging compatibility with Java 17                                                   |
| it-infrastructure-java-upgrade-migration           | Updates the Java docker image version in the integration test docker module to JDK 17                                                                                        |
| log4j-maven-shade-plugin-migration                 | Updates the Maven Shade Plugin with the new Log4j dependency information                                                                                                     |

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

### Split Data Records for the Spark Pipeline
If your spark pipeline is using `aissemble-data-records-separate-module` profile for your data records, you must add the `<version>` tag for
the `jackson-mapper-asl` dependency artifact in the root pom.xml file to enable the build.
```xml
        <dependency>
            <groupId>org.codehaus.jackson</groupId>
            <artifactId>jackson-mapper-asl</artifactId>
  +         <version>${version.jackson.mapper.asl}</version>
        </dependency>
```


## Conditional Steps

### For projects that have customized the Spark Operator Service Account permissions
The service account for the pipeline invocation service is now separated from spark operator and configured solely for the service.
If you added any custom configurations to the `sparkoperator` service account pertaining to the pipeline invocation service, you will need to migrate the related changes to the new `pipeline-invocation-service-sa`. Refer to Pipeline Invocation Helm Chart [README](https://github.com/boozallen/aissemble/blob/dev/extensions/extensions-helm/extensions-helm-pipeline-invocation/aissemble-pipeline-invocation-app-chart/README.md) for detail.

## Final Steps - Required for All Projects
### Finalizing the Upgrade
1. Run `./mvnw org.technologybrewery.baton:baton-maven-plugin:baton-migrate` to apply the automatic migrations
2. Run `./mvnw clean install` and resolve any manual actions that are suggested
    - **NOTE:** This will update any aiSSEMBLE dependencies in 'pyproject.toml' files automatically
3. Repeat the previous step until all manual actions are resolved

# What's Changed
- `pyproject.toml` files updated to allow for Python version `>=3.8`.
- All SageMaker modules and corresponding references were removed in commit bdcbb409.