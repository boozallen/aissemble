# Major Additions

## Universal Configurations Store
The Configuration Store is a tool that enables the various configurations for a project to be centrally defined and managed. It then provides a standardized way of accessing them, allowing the environment specific configurations to be dynamically provided to the their respective resources within the project at runtime. See the [official documentation](https://boozallen.github.io/aissemble/aissemble/current-dev/configuration-store.html) for more details on leveraging the configuration store.

## aiSSEMBLE Infrastructure Helm Chart
Created a helm chart with the necessary infrastructure for deploying your aiSSEMBLE project. This chart includes support for [Argo CD](https://argo-cd.readthedocs.io/en/stable/), [Jenkins](https://www.jenkins.io/), and [Nginx Ingress](https://docs.nginx.com/nginx-ingress-controller/). See the [chart README](https://github.com/boozallen/aissemble/tree/dev/extensions/extensions-helm/aissemble-infrastructure-chart#readme) for more details. 

## Spark Infrastructure v2 Helm Chart
The following Helm charts have been migrated to the v2 structure and combined into a single `spark-infrastructure` chart. To migrate your helm charts to use the v2 pattern, follow the instruction in the [technical documentation](https://boozallen.github.io/aissemble/aissemble/current/containers.html#_kubernetes_artifacts_upgrade).
- Spark Infrastructure
- Hive Metastore Service
- Hive Metastore Database

With this new chart, the `aissemble-hive-mysql` image  is no longer in use and is deprecated.  It will no longer be updated or maintained.  If you are on an older version of the Spark infrastructure charts, you can continue to use the 1.8 version of the `aissemble-hive-mysql` image.  However, we recommend upgrading to the new v2 spark-infrastructure chart to take full advantage of future fixes and improvements.

## ArgoCD Deployment Branch
Resolved issue when deploying with ArgoCD where apps would fail to utilize the current deploy job branch parameter. Now ArgoCD deployments will use the correct deploy branch when performing test deployments on branches other than `dev`.

# Breaking Changes
_[A short bulleted list of changes that will cause downstream projects to be partially or wholly inoperable without changes. Instructions for those changes should live in the How To Upgrade section]_
Note: instructions for adapting to these changes are outlined in the upgrade instructions below.

- Projects MUST upgrade to the new v2 spark-infrastructure chart in order to retain functionality for data-delivery pipelines.

# Known Issues
There are no known issues with the 1.9.0 release.

# Known Vulnerabilities
| Date<br/>identified | Vulnerability | Severity | Package | Affected <br/>versions | CVE | Fixed <br/>in |
|---------------------|---------------|----------|---------|------------------------|-----|---------------|

# Recommended Kubernetes Version
aiSSEMBLE recommends any consumer be on a minimum Kubernetes version of 1.30. This is due to security 
findings in 1.29. For more information on Kubernetes current security findings, view their 
[CVE feed](https://kubernetes.io/docs/reference/issues-security/official-cve-feed/). If using AWS EKS, please follow 
[AWS documentation](https://docs.aws.amazon.com/eks/latest/userguide/update-cluster.html) on upgrading your clusters 
and node groups.

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
| ml-flow-dockerfile-migration                       | Updates the MLFlow's Dockerfile to use the bitnami/mlflow image as a base instead of the deprecated boozallen/aissemble-mlflow image"                                      |
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

## Final Steps - Required for All Projects
### Finalizing the Upgrade
1. Run `./mvnw org.technologybrewery.baton:baton-maven-plugin:baton-migrate` to apply the automatic migrations
2. Run `./mvnw clean install` and resolve any manual actions that are suggested
    - **NOTE:** This will update any aiSSEMBLE dependencies in 'pyproject.toml' files automatically
3. Repeat the previous step until all manual actions are resolved

# What's Changed
- MLFlow Chart version upgraded from `0.2.1` to `1.4.22`.
- `aissemble-mlflow` Docker image using MLFlow 2.3.1 was replaced with `bitnami/mlflow:2.15.1-debian-12-r0`  Docker image using MLFlow 2.15.1.
- Airflow Chart version upgraded from `1.10.0` to `1.15.0`.
- `aissemble-airflow` Docker image using Airflow 2.6.2 was replaced with `apache/airflow`  Docker image using Airflow 2.9.3.
