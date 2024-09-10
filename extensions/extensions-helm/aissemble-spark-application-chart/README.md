# aiSSEMBLE&trade; Spark Application Helm Chart
Baseline Helm chart for packaging and deploying spark applications. The chart is built and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory.

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the image and tag with the desired image built from the aiSSEMBLE-generated spark application image. It is intended to be used with value overrides that are specific to an aiSSEMBLE generated pipeline. For example:
```bash
helm install pipeline-driver oci://ghcr.io/boozallen/aissemble-spark-application-chart --version <AISSEMBLE-VERSION>
--values <PATH-TO-VALUES-FILE(S)>
```
**Note**: *the version should match the aiSSEMBLE project version.*

# Properties
There are two parts to the chart: support for generating a SparkApplication definition, and for creating a service to expose the application for debugging purposes. 

To set properties on the SparkApplication, configure it under the `sparkApp` except the metadata.name and metadata.namespace. To configure the service properties, it will be configured under the `service`.
For more information on what properties can be configured for SparkApplication, see the [Spark Operator documentation](https://github.com/kubeflow/spark-operator/blob/master/docs/user-guide.md).

| Property                                        | Description                                                                        | Required Override | Default                                         |
|-------------------------------------------------|------------------------------------------------------------------------------------|-------------------|-------------------------------------------------|
| metadata.namespace                              | The kubernetes namespace for the pipeline                                          | No                | default                                         |
| sparkApp.spec.dynamicAllocation.enabled         | Enables dynamic scaling of Spark executors                                         | No                | true                                            |
| sparkApp.spec.dynamicAllocation.initialExecutor | Initial number of executors to run if dynamic allocation is enabled.               | No                | 0                                               |
| sparkApp.spec.dynamicAllocation.maxExecutors    | Upper bound for the number of executors if dynamic allocation is enabled.          | No                | 4                                               |
| sparkApp.spec.dynamicAllocation.minExecutors    | Lower bound for the number of executors if dynamic allocation is enabled.          | No                | 0                                               |
| sparkApp.spec.driver.coreLimit                  | The limit on CPU cores for the driver pod                                          | No                | "1200m"                                         |
| sparkApp.spec.driver.cores                      | Number of CPU cores allocated to the driver pod                                    | No                | 1                                               |
| sparkApp.spec.driver.memory                     | Amount of memory allocated to the driver pod                                       | No                | "512m"                                          |
| sparkApp.spec.executor.coreLimit                | The limit on CPU cores for the executor pod                                        | No                | "1200m"                                         |
| sparkApp.spec.executor.cores                    | Number of CPU cores allocated to the executor pod                                  | No                | 1                                               |
| sparkApp.spec.executor.labels.version           | The executor version label                                                         | No                | 3.4.0                                           |
| sparkApp.spec.executor.memory                   | Amount of memory allocated to the executor pod                                     | No                | "512m"                                          |
| sparkApp.spec.mode                              | The Spark operation mode (cluster, standalone, etc.)                               | No                | cluster                                         |
| sparkApp.spec.deps.repositories                 | Additional Maven repositories to reference for dependency resolution               | No                | NB: OSS: update with aissemble maven repository |
| sparkApp.spec.imagePullPolicy                   | The image pull policy for the spark-worker image                                   | No                | IfNotPresent                                    |
| sparkApp.spec.restartPolicy.type                | The restart policy for the pipeline                                                | No                | Never                                           |
| sparkApp.spec.serviceAccount                    | The name of the service account to use for Spark                                   | No                | spark                                           |
| sparkApp.spec.sparkConfigMap                    | The name of the ConfigMap that holds common configurations for all Spark pipelines | No                | spark-config                                    |
| sparkApp.spec.sparkVersion                      | The spark version                                                                  | No                | 3.4.0                                           |
| sparkApp.spec.type                              | Whether the pipeline is written in Java or Python                                  | Yes               | "placeholder"                                   |
| service.enabled                                 | Whether to create a Service to expose ports on the Spark pipeline application      | No                | false                                           |
| service.spec.ports.name                         | A friendly name for a service port definition                                      | No                | "debug"                                         |
| service.spec.ports.port                         | The port to be exposed                                                             | No                | 4747                                            |
| service.spec.ports.targetPort                   | The port that the exposed port should map to                                       | No                | 4747                                            |

## Default Spark Properties
The following Spark settings are set by default to ensure compatibility with aiSSEMBLE-provided resources.  They may be 
overridden as needed, and each is nested beneath `sparkApp.spec.sparkConf`:

| Property                                 | Value                                          |
|------------------------------------------|------------------------------------------------|
| spark.hive.server2.thrift.port           | "10000"                                        |
| spark.hive.server2.thrift.http.port      | "10001"                                        |
| spark.hive.server2.transport.mode        | "http"                                         |
| spark.hadoop.fs.s3a.path.style.access    | "true"                                         |
| spark.hive.server2.thrift.http.path      | "cliservice                                    |
| spark.hive.metastore.schema.verification | "false"                                        |
| spark.hive.metastore.warehouse.dir       | "s3a://spark-infrastructure/warehouse"         |
| spark.hive.metastore.uris                | "thrift://hive-metastore-service:9083/default" |
| spark.eventLog.dir                       | "/opt/spark/spark-events"                      |

# Interoperability with the aiSSEMBLE Spark Operator Helm Chart
The aiSSEMBLE Spark Application Helm Chart is largely intended to be used in conjunction with the [aiSSEMBLE Spark
Operator Helm Chart](../aissemble-spark-operator-chart/README.md).  As such, some features of this chart are designed to
interact with the Spark Operator chart.

## Shared Ivy Cache
To support a shared ivy cache between multiple SparkApplication instances, this chart automatically creates a `volume`
and `volumeMount` using the PersistentVolumeClaim created by the Spark Operator chart.  This chart assumes the
default configuration for the Spark Operator chart. In order to ensure compatibility, some manual changes may need to be
made if you make any of the following customizations:
 - Disabling the `ivyCache` feature in the Spark Operator chart
 - Changing `ivyCache.name` in the Spark Operator chart
 - Adding volumes/volumeMounts to your SparkApplication definition

## Shared Spark History Events
To support shared Spark history events between multiple SparkApplication instances, this chart automatically creates a 
`volume` and `volumeMount` using the PersistentVolumeClaim created by the Spark Infrastructure chart. This chart 
assumes the default configuration for the Spark Infrastructure chart. In order to ensure compatibility, some manual 
changes may need to be made if you make any of the following customizations:
- Disabling the `aissemble-spark-history-chart` feature in the Spark Infrastructure chart
- Changing `aissemble-spark-history-chart.eventVolume.name` in the Spark Infrastructure chart
- Adding volumes/volumeMounts to your SparkApplication definition

### Disabling the Ivy Cache and/or Spark History
If you decide to disable the `ivyCache` feature or the `aissemble-spark-history-chart`, you will need to manually 
remove one of or both the `volume` and `volumeMount` from the `sparkApp` definition
in your values file, e.g.:
```yaml
sparkApp:
  spec:
    volumes: []
    driver:
      volumeMounts: []
    executor:
      volumeMounts: []
```

### Changing the Ivy Cache Name
If you change the default name of the `PersistentVolumeClaim` created by the Spark Operator chart, you will need to update
the `volume` in the `sparkApp` definition in your values file, e.g.:
```yaml
sparkApp:
  spec:
    volumes:
      - name: spark-ivy-cache
        persistentVolumeClaim:
          claimName: my-ivy-cache
```

### Changing the Spark History eventVolume name
If you change the default name of the `PersistentVolumeClaim` created by the Spark Infrastructure chart, you will 
need to update the `volume` in the `sparkApp` definition in your values file, e.g.:
```yaml
sparkApp:
  spec:
    volumes:
      - name: spark-events
        persistentVolumeClaim:
          claimName: my-spark-events-claim
```

### Adding Volumes/VolumeMounts
Because lists in YAML are immutable, if you add volumes or volumeMounts to your SparkApplication definition you will need
to manually include the `spark-ivy-cache` and `spark-events` volumes and/or volumeMounts to your values file, e.g.:
```yaml 
sparkApp:
  spec:
    volumes:
      - name: spark-ivy-cache
        persistentVolumeClaim:
          claimName: my-ivy-cache
      - name: spark-events
        persistentVolumeClaim:
          claimName: spark-events-claim
      - name: my-volume
        emptyDir: {}
    driver:
      volumeMounts:
        - name: spark-ivy-cache
          mountPath: /tmp/.ivy2
        - name: spark-events
          mountPath: "/opt/spark/spark-events"
        - name: my-volume
          mountPath: /tmp/my-volume 
    executor:
      volumeMounts:
        - name: spark-ivy-cache
          mountPath: /tmp/.ivy2
        - name: spark-events
          mountPath: "/opt/spark/spark-events"
        - name: my-volume
          mountPath: /tmp/my-volume
```

# Migration from aiSSEMBLE v1 Helm Charts
If you are migrating from the v1 version of the spark-application chart, use the tables below to apply any existing customizations from the old chart to the new v2 chart.

## Property Location
All properties listed in the **Properties** section have been moved to the parent chart. If any properties are set to the default value, we recommend removing them from your values file entirely.

All new property locations configured for SparkApplication should include the prefix `aissemble-spark-application-chart.sparkApp` except below properties:

| Old Property Location | New Property Location                             | 
|-----------------------|---------------------------------------------------|
| metadata.name         | aissemble-spark-application-chart.metadata.name   | 
| metadata.namespace    | aissemble-spark-application-chart.namespace       | 
| spec.serviceEnabled   | aissemble-spark-application-chart.service.enabled |

And all new property locations for service configuration should include the prefix `aissemble-spark-application-chart` only. 

*Note: To enable the service configuration, you need to set* `aissemble-spark-application-chart.sparkApp.spec.serviceEnabled=true`


