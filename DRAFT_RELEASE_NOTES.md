# Major Additions

## Universal Configurations Store
The Configuration Store is a tool that enables the various configurations for a project to be centrally defined and managed. It then provides a standardized way of accessing them, allowing the environment specific configurations to be dynamically provided to the their respective resources within the project at runtime. See the [official documentation](https://boozallen.github.io/aissemble/aissemble/current/configuration-store.html) for more details on leveraging the configuration store.

## aiSSEMBLE Infrastructure Helm Chart
The aiSSEMBLE Infrastructure Helm Chart contains the necessary infrastructure for deploying your project within a single umbrella chart. This chart includes support for [Argo CD](https://argo-cd.readthedocs.io/en/stable/), [Jenkins](https://www.jenkins.io/), and [Nginx Ingress](https://docs.nginx.com/nginx-ingress-controller/). See the [chart README](https://github.com/boozallen/aissemble/tree/dev/extensions/extensions-helm/aissemble-infrastructure-chart#readme) for more details. 

## Spark Infrastructure v2 Helm Chart
The following Helm charts have been migrated to the v2 structure and combined into a single `spark-infrastructure` chart. To migrate your Helm charts to use the v2 pattern, follow the instructions in the [technical documentation](https://boozallen.github.io/aissemble/aissemble/current/containers.html#_kubernetes_artifacts_upgrade).
- Spark Infrastructure
- Hive Metastore Service
- Hive Metastore Database

With this new chart, the `aissemble-hive-mysql` image is no longer being used. As a result, the image is deprecated and will not be updated or maintained moving forward. If you choose to remain on an older version of the Spark Infrastructure charts, you can continue to use the `1.8` version of the `aissemble-hive-mysql` image. However, we recommend upgrading to the new v2 spark-infrastructure chart to take full advantage of future fixes and improvements.

## Helm Chart Updates
[MLFlow Helm Chart](https://github.com/boozallen/aissemble/tree/dev/extensions/extensions-helm/aissemble-mlflow-chart) parent version upgraded from `0.2.1` to `1.4.22`. This includes an update to use the community docker image `bitnami/mlflow:2.15.1-debian-12-r0` instead of the deprecated `boozallen/aissemble-mlflow:1.7.0` image. This new image updates the MLFlow version from `2.3.1` to `2.15.1`.

[Airflow Helm Chart](https://github.com/boozallen/aissemble/tree/dev/extensions/extensions-helm/aissemble-airflow-chart) parent version upgraded from `1.10.0` to `1.15.0`. This includes an update to use the community docker image `apache/airflow:2.9.3` instead of the deprecated `boozallen/aissemble-airflow:1.7.0` image. This new image updates the Airflow version from `2.6.2` to `2.9.3`.

[Kafka Helm Chart](https://github.com/boozallen/aissemble/tree/dev/extensions/extensions-helm/aissemble-kafka-chart) updated to use the community docker image `bitnami/kafka:3.5.1-debian-11-r1` instead of the deprecated `boozallen/aissemble-kafka:1.7.0`. This new image remains on the same Kafka version `3.5.1`.

## ArgoCD Deployment Branch
Resolved issue when deploying with ArgoCD where apps would fail to utilize the current deploy job branch parameter. Now ArgoCD deployments will use the correct deploy branch when performing test deployments on branches other than the default `dev`.

# Breaking Changes
Note: instructions for adapting to these changes are outlined in the upgrade instructions below.

- Projects MUST upgrade to the new v2 `spark-infrastructure` chart in order to retain functionality for data-delivery pipelines.

# Known Issues
There are no known issues with the `1.9.0` release.

# Known Vulnerabilities
| Date<br/>identified | Vulnerability | Severity | Package | Affected <br/>versions | CVE | Fixed <br/>in |
|---------------------|---------------|----------|---------|------------------------|-----|---------------|

# Recommended Kubernetes Version
aiSSEMBLE recommends any consumer be on a minimum Kubernetes version of `1.30` due to security 
findings in `1.29`. For more information on Kubernetes current security findings, view their 
[CVE feed](https://kubernetes.io/docs/reference/issues-security/official-cve-feed/). If using AWS EKS, please follow 
[AWS documentation](https://docs.aws.amazon.com/eks/latest/userguide/update-cluster.html) on upgrading your clusters 
and node groups.

# How to Upgrade
The following steps will upgrade your project to `1.9`. These instructions consist of multiple phases:
- Automatic Upgrades - no manual action required
- Precondition Steps - needed in all situations
- Conditional Steps (e.g., Python steps, Java steps, if you use Metadata, etc)
- Final Steps - needed in all situations

## Automatic Upgrades
To reduce burden of upgrading aiSSEMBLE, the Baton project is used to automate the migration of some files to the new version.  These migrations run automatically when you build your project, and are included by default when you update the `build-parent` version in your root POM.  Below is a description of all of the Baton migrations that are included with this version of aiSSEMBLE.

| Migration Name                                          | Description                                                                                                                                                                                                                               |
|-----------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| upgrade-tiltfile-aissemble-version-migration            | Updates the aiSSEMBLE version within your project's Tiltfile                                                                                                                                                                              |
| upgrade-v2-chart-files-aissemble-version-migration                                                                          | Updates the Helm chart dependencies within your project's deployment resources (`<YOUR_PROJECT>-deploy/src/main/resources/apps/`) to use the latest version of the aiSSEMBLE                                                                     |
| upgrade-v1-chart-files-aissemble-version-migration                                                                          | Updates the docker image tags within your project's deployment resources (`<YOUR_PROJECT>-deploy/src/main/resources/apps/`) to use the latest version of the aiSSEMBLE                                                                           |
| ml-flow-dockerfile-migration                                                                                                | Updates the MLFlow's Dockerfile to use the `bitnami/mlflow` image as a base instead of the deprecated `boozallen/aissemble-mlflow image`                                                                                                         |
| airflow-dockerfile-migration                                                                                                | Updates the Airflow's Dockerfile to use the `bitnami/airflow` image as a base instead of the deprecated `boozallen/aissemble-airflow image`                                                                                                      |
| update-data-access-thrift-endpoint-migration                                                                                | For projects using the default data-access thrift endpoint, updates to the new endpoint associated with v2 `spark-infrastructure`                                                                                                                |
| argocd-value-file-sync-policy-configuration-migration                                                                       | Updates the ArgoCD values files (`<YOUR_PROJECT>-deploy/src/main/resources/`) to include the syncPolicy values to enable the Configuration Store to deploy first on your cluster.                                                                |
| argocd-template-sync-policy-configuration-migration                                                                         | Updates the ArgoCD template files (`<YOUR_PROJECT>-deploy/src/main/resources/templates/`) to include the syncPolicy helm function to enable the Configuration Store to deploy first on your cluster.                                             |
| argocd-spark-operator-sync-policy-configuration-migration                                                                   | Similar to `argocd-template-sync-policy-configuration-migration` but specificaly for the spark-operator template to account for the ServerSideApply value. If this migration fails it will print instructions for manually updating to the logs. |

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
To start your aiSSEMBLE upgrade, update your project's pom.xml to use the `1.9.0` version of the build-parent:
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
