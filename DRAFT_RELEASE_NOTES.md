# Major Additions

# Breaking Changes
_<A short bulleted list of changes that will cause downstream projects to be partially or wholly inoperable without changes. Instructions for those changes should live in the How To Upgrade section>_
Note: instructions for adapting to these changes are outlined in the upgrade instructions below.

# Known Issues
There are no known issues with the 1.8.1 release.

# Known Vulnerabilities
| Date<br/>identified | Vulnerability | Severity | Package | Affected <br/>versions | CVE | Fixed <br/>in |
|---------------------|---------------|----------|---------|------------------------|-----|---------------|

# How to Upgrade
The following steps will upgrade your project to 1.8.1. These instructions consist of multiple phases:
- Automatic Upgrades - no manual action required
- Precondition Steps - needed in all situations
- Conditional Steps (e.g., Python steps, Java steps, if you use Metadata, etc)
- Final Steps - needed in all situations

## Automatic Upgrades
To reduce burden of upgrading aiSSEMBLE, the Baton project is used to automate the migration of some files to the new version.  These migrations run automatically when you build your project, and are included by default when you update the `build-parent` version in your root POM.  Below is a description of all of the Baton migrations that are included with this version of aiSSEMBLE.

| Migration Name                                             | Description                                                                                                                                                                |
|------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| upgrade-tiltfile-aissemble-version-migration               | Updates the aiSSEMBLE version within your project's Tiltfile                                                                                                               |
| upgrade-v2-chart-files-aissemble-version-migration         | Updates the helm chart dependencies within your project's deployment resources (<YOUR_PROJECT>-deploy/src/main/resources/apps/) to use the latest version of the aiSSEMBLE |
| upgrade-v1-chart-files-aissemble-version-migration         | Updates the docker image tags within your project's deployment resources (<YOUR_PROJECT>-deploy/src/main/resources/apps/) to use the latest version of the aiSSEMBLE       |

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
To start your aiSSEMBLE upgrade, update your project's pom.xml to use the 1.8.1 version of the build-parent:
   ```xml
   <parent>
       <groupId>com.boozallen.aissemble</groupId>
       <artifactId>build-parent</artifactId>
       <version>1.8.1</version>
   </parent>
   ```

## Conditional Steps


## Final Steps - Required for All Projects
### Finalizing the Upgrade
1. Run `./mvnw org.technologybrewery.baton:baton-maven-plugin:baton-migrate` to apply the automatic migrations
1. Run `./mvnw clean install` and resolve any manual actions that are suggested
    - **NOTE:** This will update any aiSSEMBLE dependencies in 'pyproject.toml' files automatically
1. Repeat the previous step until all manual actions are resolved

# What's Changed