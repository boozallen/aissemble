# Major Additions

* Python modules were renamed to reflect aiSSEMBLE. These include the following. 

| Old Python Module                       | New Python Module                                 |
| --------------------------------------- |---------------------------------------------------|
| foundation-core-python                  | aissemble-core-python                             |
| foundation-model-training-api           | aissemble-foundation-model-training-api           |
| foundation-versioning-service           | aissemble-foundation-versioning-service           |
| foundation-drift-detection-client       | aissemble-foundation-drift-detection-client       |
| foundation-encryption-policy-python     | aissemble-foundation-encryption-policy-python     |
| foundation-model-lineage                | aissemble-foundation-model-lineage                |
| foundation-data-lineage-python          | aissemble-foundation-data-lineage-python          |
| foundation-messaging-python-client      | aissemble-foundation-messaging-python-client      |
| foundation-pdp-client-python            | aissemble-foundation-pdp-client-python            |
| foundation-transform-core-python        | aissemble-Foundation-transform-core-python        |
| extensions-model-training-api-sagemaker | aissemble-extensions-model-training-api-sagemaker |
| extensions-data-delivery-spark-py       | aissemble-extensions-data-delivery-spark-py       |
| extensions-encryption-valut-python      | aissemble-extensions-encryption-vault-python      |
| extensions-transform-spark-python       | aissemble-extensions-transform-spark-python       |
| test-data-delivery-pyspark-model        | aissemble-test-data-delivery-pyspark-model        |
| test-data-delivery-pyspark-model-basic  | aissemble-test-data-delivery-pyspark-model-basic  |
| machine-learning-inference              | aissemble-machine-learning-inference              |
| machine-learning-training               | aissemble-machine-learning-training               |
| machine-learning-training-base          | aissemble-machine-learning-training-base          |
| machine-learning-sagemaker-training     | aissemble-machine-learning-sagemaker-training     |

* Helm Charts and their relevant modules have been renamed to the following:

| Old Module Name                         | New Helm Chart and Module Name          |
|-----------------------------------------|-----------------------------------------|
| extensions-helm-airflow                 | aissemble-airflow-chart                 |
| extensions-helm-data-access             | aissemble-data-access-chart             |
| extensions-helm-elasticsearch           | aissemble-elasticsearch-chart           |
| extensions-helm-elasticsearch-operator  | aissemble-elasticsearch-operator-chart  |
| extensions-helm-fastapi                 | aissemble-fastapi-chart                 |
| extensions-helm-hive-metastore-db       | aissemble-hive-metastore-db-chart       |
| extensions-helm-hive-metastore-service  | aissemble-hive-metastore-service-chart  |
| extensions-helm-inference               | aissemble-inference-chart               |
| extensions-helm-jenkins                 | aissemble-jenkins-chart                 |
| extensions-helm-kafka                   | aissemble-kafka-chart                   |
| extensions-helm-keycloak                | aissemble-keycloak-chart                |
| extensions-helm-lineage-http-consumer   | aissemble-lineage-http-consumer-chart   |
| extensions-helm-localstack              | aissemble-localstack-chart              |
| extensions-helm-metadata                | aissemble-metadata-chart                |
| extensions-helm-mlflow                  | aissemble-mlflow-chart                  |
| extensions-helm-pipeline-invocation     | aissemble-pipeline-invocation-chart     |
| extensions-helm-pipeline-invocation-lib | aissemble-pipeline-invocation-lib-chart |
| extensions-helm-policy-decision-point   | aissemble-policy-decision-point-chart   |
| extensions-helm-quarkus                 | aissemble-quarkus-chart                 |
| extensions-helm-sealed-secrets          | aissemble-sealed-secrets-chart          |
| extensions-helm-spark-application       | aissemble-spark-application-chart       |
| extensions-helm-spark-operator          | aissemble-spark-operator-chart          |
| extensions-helm-vault                   | aissemble-vault-chart                   |
| extensions-helm-versioning              | aissemble-versioning-chart              |

## OpenLineage Namespace Conventions
Conventions for setting namespaces when leveraging `Data Lineage` has been updated to better follow [OpenLineage's guidelines](https://openlineage.io/docs/spec/naming/). Moving forward, namespaces should be defined in the `data-lineage.properties` file, such that Jobs are tied to pipelines and Datasets are tied to data sources. This is a departure from the old pattern of one single namespace property (`data.lineage.namespace`) being leveraged for an entire project. Refer to the [GitHub docs](https://boozallen.github.io/aissemble/current-dev/lineage-medatada-capture-overview.html#_configuration) for updated guidance. Usage of the `data.lineage.namespace` property in a project's `data-lineage.properties` file will be supported as a fallback but should not be used in practice.

# Breaking Changes
Note instructions for adapting to these changes are outlined in the upgrade instructions below.  
* The maven property `version.clean.plugin` was changed to `version.maven.clean.plugin` causing the `*-deploy/pom.xml` 
  to be invalid

## DataLineage and ModelLineage Event Changes
To associate the pipeline step's lineage event with the pipeline's, we have created a pipeline level lineage event, and a way
for each pipeline step's lineage event to be associated with the pipeline's lineage run event. 

We have also made adjustments regarding customizing the lineage event so that we can customize the lineage event
based on the event type. The below functions have been removed, and replaced by event type-specific functions:

| Python Method Signature                                            | Java Method Signature                                    |
| ------------------------------------------------------------------ | -------------------------------------------------------- |
| create_run(self) → Run                                             | Run createRun()                                          |
| create_job(self) → Job                                             | Job createJob()                                          |
| create_run_event(self, run: Run, job: Job, status: str) → RunEvent | RunEvent createRunEvent(Run run, Job job, String status) |

If you have overridden these functions in your project, please refer to below [Customize Lineage Event] section to make changes accordingly.

The default producer value that will be generated into the data-lineage.properties file is now pulled from the scm url tag in the project's
root pom.xml file.

# Known Issues
There are no known issues with the 1.7.0 release.

# Known Vulnerabilities
| Date<br/>identified | Vulnerability | Severity | Package | Affected <br/>versions | CVE | Fixed <br/>in |
| ------------------- | ------------- | -------- | ------- | ---------------------- | --- | ------------- |

# How to Upgrade
The following steps will upgrade your project to 1.7. These instructions consist of multiple phases:
- Automatic Upgrades - no manual action required
- Precondition Steps - needed in all situations
- Conditional Steps (e.g., Python steps, Java steps, if you use Metadata, etc)
- Final Steps - needed in all situations

## Automatic Upgrades
To reduce burden of upgrading aiSSEMBLE, the Baton project is used to automate the migration of some files to the new version.  These migrations run automatically when you build your project, and are included by default when you update the `build-parent` version in your root POM.  Below is a description of all of the Baton migrations that are included with this version of aiSSEMBLE.

| Migration Name                                     | Description                                                                                                                                                                                              |
| -------------------------------------------------- |----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| upgrade-tiltfile-aissemble-version-migration       | Updates the aiSSEMBLE version within your project's Tiltfile                                                                                                                                             |
| upgrade-v2-chart-files-aissemble-version-migration | Updates the helm chart dependencies within your project's deployment resources (<YOUR_PROJECT>-deploy/src/main/resources/apps/) to use the latest version of the aiSSEMBLE                               |
| upgrade-v1-chart-files-aissemble-version-migration | Updates the docker image tags within your project's deployment resources (<YOUR_PROJECT>-deploy/src/main/resources/apps/) to use the latest version of the aiSSEMBLE                                     |
| upgrade-mlflow-v2-external-s3-migration            | Update the mlflow V2 deployment (if present) in your project to utilize Localstack for local development and SealedSecrets for remote deployments                                                        |
| upgrade-spark-application-s3-migration             | Update the pipeline SparkApplication(s) (if present) in your project to utilize Localstack for local development and SealedSecrets for remote deployments |
| upgrade-foundation-extension-python-package-migration                              | Updates the pyproject.toml files within your projects pipelines folder (<YOUR_PROJECT>-pipelines) to use the updated aiSSEMBLE foundation and extension Python packages with the latest naming convention | 
|upgrade-helm-chart-files-names-migration            | Updates the Chart.yaml and values*.yaml files within your project's deploy folder (<YOUR_PROJECT>-deploy) to use the new Helm chart naming convention (aissemble-\<chart-name\>-chart)                     |
| upgrade-dockerfile-pip-install-migration           | Updates dockerfiles such that python dependency installations fail during the build, rather than at runtime |

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

## Precondition Steps

### Beginning the Upgrade - Required for All Projects
To start your aiSSEMBLE upgrade, update your project's pom.xml to use the 1.7.0 version of the build-parent:
   ```xml
   <parent>
       <groupId>com.boozallen.aissemble</groupId>
       <artifactId>build-parent</artifactId>
       <version>1.7.0</version>
   </parent>
   ```

### Delete the Old maven-clean-plugin Version - Required for All Projects
In order to follow the standard naming conventions for maven properties, the original property used for the 
maven-clean-plugin version no longer exists. To resolve the Maven failure this causes, delete the version from the 
plugin in *-deploy/pom.xml. This can be achieved with:
```
sed -i'' -e '/version.clean.plugin/d' *-deploy/pom.xml
```

## Conditional Steps

### Upgrade Steps for Projects Leveraging Data Lineage

#### Updated Namespace Conventions with Data Lineage
In order to follow standards for defining namespaces for OpenLineage Jobs and Datasets, the following steps can be taken to leverage proper namespace conventions:
1. [Optional] If you are already setting the `data.lineage.namespace` value in your `<project-name>-docker/<project-name>-spark-worker-docker/src/main/resources/krausening/base/data-lineage.properties` file, it is recommended that you follow the [configuration documentation]((https://boozallen.github.io/aissemble/current-dev/lineage-medatada-capture-overview.html#_configuration)) and set `data.lineage.<pipeline>.namespace` and `data.lineage.<pipeline>.<step>.namespace` instead, and remove `data.lineage.namespace` property.
2. If you project does not have a `data-lineage.properties` file, one will be generated during your next build.
3. If your pipeline leverages any lineage Datasets, you must define a namespace for each dataset, per the [GitHub docs guidance](https://boozallen.github.io/aissemble/current-dev/lineage-medatada-capture-overview.html#_configuration):
```text
data.lineage.<dataset-name>.namespace=<dataset's-source-name>
```
Note: An exception will be thrown if both the dataset's namespace and `data.lineage.namespace` are not configured.

#### Associate Step Lineage Events to Pipeline
The data lineage now supports pipeline level lineage run event, which provides the parent run facet for all the step level lineage events, and helps to preserve pipeline-step job hierarchy and to tie all the step level lineage events' job together.

##### pyspark pipeline driver class
* add PipelineBase import
* add the `PipelineBase().record_pipeline_lineage_start_event()` before all the steps' executions
* add the `PipelineBase().record_pipeline_lineage_complete_event()` after all the steps' executions
```text
  from krausening.logging import LogManager
+ from first_process.generated.pipeline.pipeline_base import PipelineBase

  if __name__ == "__main__":
      logger.info("STARTED: FirstProcess driver")
+     PipelineBase().record_pipeline_lineage_start_event()
      Ingest1().execute_step()
      ...
      Ingest4().execute_step()
+     PipelineBase().record_pipeline_lineage_complete_event()
```

##### spark pipeline driver class
* add PipelineBase import
* add the `PipelineBase.getInstance().recordPipelineLineageStartEvent();` before all the steps' executions
* add the `PipelineBase.getInstance().recordPipelineLineageCompleteEvent();` after all the steps' executions
```text
+ import com.boozallen.pipeline.PipelineBase;
  import org.slf4j.Logger;
  ...

  public static void main(String[] args) {
    logger.info("STARTED: {} driver", "SparkPipeline");
    SparkPipelineBaseDriver.main(args);

+   PipelineBase.getInstance().recordPipelineLineageStartEvent();
    ...
    final Step2 step2 = CDI.current().select(Step2.class, new Any.Literal()).get();
    CompletionStage<Void> step2Result = step2.executeStep();
    ...
+   PipelineBase.getInstance().recordPipelineLineageCompleteEvent();
```

#### Customize Lineage Event
Please follow the [generated code](https://boozallen.github.io/aissemble/current/lineage-medatada-capture-overview.html#_what_gets_generated) instructions to customize the lineage event accordingly.


## Final Steps

### Finalizing the Upgrade - Required for All Projects
1. Run `mvn clean install` and resolve any manual actions that are suggested
    - **NOTE:** This will update any aiSSEMBLE dependencies in 'pyproject.toml' files automatically
2. Repeat the previous step until all manual actions are resolved

# What's Changed
