# Post Release

# Major Additions


# Breaking Changes
_<A short bulleted list of changes that will cause downstream projects to be partially or wholly inoperable without changes. Instructions for those changes should live in the How To Upgrade section>_
Note instructions for adapting to these changes are outlined in the upgrade instructions below.

- Projects MUST upgrade to the new v2 spark-infrastructure chart in order to retain functionality for data-delivery pipelines.

# Known Issues
There are no known issues with the 1.9.0 release.

# Known Vulnerabilities
| Date<br/>identified | Vulnerability | Severity | Package | Affected <br/>versions | CVE | Fixed <br/>in |
| ------------------- | ------------- | -------- | ------- | ---------------------- | --- | ------------- |

# How to Upgrade
The following steps will upgrade your project to 1.9. These instructions consist of multiple phases:
- Automatic Upgrades - no manual action required
- Precondition Steps - needed in all situations
- Conditional Steps (e.g., Python steps, Java steps, if you use Metadata, etc)
- Final Steps - needed in all situations

## Automatic Upgrades
To reduce burden of upgrading aiSSEMBLE, the Baton project is used to automate the migration of some files to the new version.  These migrations run automatically when you build your project, and are included by default when you update the `build-parent` version in your root POM.  Below is a description of all of the Baton migrations that are included with this version of aiSSEMBLE.

| Migration Name                                     | Description                                                                                                                                                                |
|----------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| upgrade-tiltfile-aissemble-version-migration       | Updates the aiSSEMBLE version within your project's Tiltfile                                                                                                               |
| upgrade-v2-chart-files-aissemble-version-migration | Updates the helm chart dependencies within your project's deployment resources (<YOUR_PROJECT>-deploy/src/main/resources/apps/) to use the latest version of the aiSSEMBLE |
| upgrade-v1-chart-files-aissemble-version-migration | Updates the docker image tags within your project's deployment resources (<YOUR_PROJECT>-deploy/src/main/resources/apps/) to use the latest version of the aiSSEMBLE       |
| update-data-access-thrift-endpoint-migration       | For projects using the default data-access thrift endpoint, updates to the new endpoint associated with v2 spark-infrastructure                                            |

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
To start your aiSSEMBLE upgrade, update your project's pom.xml to use the 1.9.0 version of the build-parent:
   ```xml
   <parent>
       <groupId>com.boozallen.aissemble</groupId>
       <artifactId>build-parent</artifactId>
       <version>1.9.0</version>
   </parent>
   ```

## Conditional Steps

### Projects With Data Delivery Pipelines
#### Migrate to the V2 Spark Infrastructure Helm Chart
This version of aiSSEMBLE brings a required upgrade to our new spark-infrastructure helm chart.  This singular
chart, following aiSSEMBLE's v2 structure, replaces the v1 charts for spark-infrastructure, hive-metastore-db, and 
hive-metastore-service.  For more information on the chart itself, see the associated [README files](https://github.com/boozallen/aissemble/tree/dev/extensions/extensions-helm/extensions-helm-spark-infrastructure). 

In order to perform this migration, the following steps should be taken: 

- Identify and remove the `fermenter-mda` executions from `<YOUR_PROJECT>-deploy/pom.xml` for 
the following profiles:
  - `hive-metastore-service-deploy`
  - `hive-metastore-db-deploy`
- Within the associated `fermenter-mda` execution in `<YOUR_PROJECT>-deploy/pom.xml`, replace references to the
`aissemble-spark-infrastructure-deploy` profile with `aissemble-spark-infrastructure-deploy-v2`
- Remove references to `hive-metastore-db` and `hive-metastore-service` from your Tiltfile
- Remove or rename the directory `<YOUR_PROJECT>-deploy/src/main/resources/apps/spark-infrastructure`
- Remove the following files from `<YOUR_PROJECT>-deploy/src/main/resources/templates/`:
  - `hive-metastore-db.yaml`
  - `hive-metastore-service.yaml`
  - `spark-infrastructure.yaml`
- Execute `./mvnw clean install -pl <YOUR_PROJECT>-deploy`
- Apply any customizations as needed to the generated `spark-infrastructure` chart

Certain settings must also be applied to each of your SparkApplications.  The following entries should be added to each
data-delivery pipeline's `resources/apps/<pipeline>-dev.yaml` file:

```yaml
sparkApp:
  spec:
    sparkConf:
      spark.hadoop.fs.s3a.endpoint: "http://s3-local:4566"
      spark.eventLog.dir: "/opt/spark/spark-events"
      spark.hive.metastore.warehouse.dir: "s3a://spark-infrastructure/warehouse"
```

## Final Steps - Required for All Projects
### Finalizing the Upgrade
1. Run `./mvnw org.technologybrewery.baton:baton-maven-plugin:baton-migrate` to apply the automatic migrations
2. Run `./mvnw clean install` and resolve any manual actions that are suggested
    - **NOTE:** This will update any aiSSEMBLE dependencies in 'pyproject.toml' files automatically
3. Repeat the previous step until all manual actions are resolved

# What's Changed
