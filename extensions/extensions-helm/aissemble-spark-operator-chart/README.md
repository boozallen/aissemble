# aiSSEMBLE&trade; Spark Operator Helm Chart
Baseline Helm chart for deploying the Spark Kubernetes Operator. Chart is built and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory. See https://github.com/kokuwaio/helm-maven-plugin for more details.

This chart is based heavily upon the original spark-On-k8s-operator upstream chart, available at https://github.com/kubeflow/spark-operator/blob/master/charts/spark-operator-chart/README.md.

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart version with the desired aiSSEMBLE version. For example:
```bash
helm install spark-operator oci://ghcr.io/boozallen/aissemble-spark-operator-chart --version <AISSEMBLE-VERSION>
```
_**NOTE:**_ *the version should match the aiSSEMBLE project version.*

# Properties

## Custom Properties
The following properties are specific to the aiSSEMBLE Spark Operator chart.

| Property                  | Description                                                                                                     | Default              |
|---------------------------|-----------------------------------------------------------------------------------------------------------------|----------------------|
| ivyCache.enabled          | Enable a shared Ivy cache for all Spark Applications (See [Shared Ivy Cache](#shared-ivy-cache))                | true                 |
| ivyCache.name             | Name of the PersistentVolumeClaim (and potentially PersistentVolume if `ivyCache.storeCacheOnNode` is `true`)   | ivy-cache            |
| ivyCache.size             | Size of the Ivy cache                                                                                           | 1Gi                  |
| ivyCache.accessModes      | [Access mode](https://kubernetes.io/docs/concepts/storage/persistent-volumes/#access-modes) for the Ivy cache   | ReadWriteOnce        |
| ivyCache.storageClass     | Storage class for the Ivy cache. Ignored if `ivyCache.storeCacheOnNode` is `true`                               |                      |
| ivyCache.storeCacheOnNode | Store the shared Ivy cache on the Node and reuse it between deployments                                         | false                |
| ivyCache.cachePathOnNode  | The location on the node for to store the reusable Ivy cache. Ignored if `ivyCache.storeCacheOnNode` is `false` | /tmp/spark-ivy-cache |


## Overridden Inherited Properties
For an exhaustive list of available properties, see the source material at https://github.com/kubeflow/spark-operator/blob/spark-operator-chart-1.4.6/charts/spark-operator-chart/README.md#values.
What follows reflects properties in the base spark-operator chart which have been overridden.  To modify these properties,
you must reference the root chart in your `values.yaml` file.  For example:

```yaml
aissemble-spark-operator-chart:
  spark-operator:
    webhook.enable: false
```

| Property                           | Description                        | Required Override | Default                                                                                         |
|------------------------------------|------------------------------------|-------------------|-------------------------------------------------------------------------------------------------|
| image.repository                   | The image repository               | No                | ghcr.io/boozallen/aissemble-spark-operator                                                      |
| image.tag                          | The image tag                      | No                | Chart.Version                                                                                   |
| webhook.enable                     | Enable webhook server              | No                | true                                                                                            |
| volumes                            | Volumes for the pod                | No                | `spark-logging=/tmp/spark-logging`, `ivy-cache=/home/spark/.ivy2`                               |
| volumeMounts                       | Volume Mounts for the pod          | No                | `spark-logging=/tmp/spark-logging`, `ivy-cache=/home/spark/.ivy2`                               |
| fullnameOverride                   | String to override release name    | No                | spark-operator                                                                                  |
| serviceAccounts.spark.name         | Name for the spark service account | No                | spark                                                                                           |
| serviceAccounts.sparkoperator.name | Name for the spark service account | No                | sparkoperator                                                                                   |
| podSecurityContext                 | Pod security context               | No                | runAsUser: 185<br/>runAsGroup: 1000<br/>fsGroup: 1000<br/>fsGroupChangePolicy: "OnRootMismatch" |

# Shared Ivy Cache

Spark uses [Ivy](https://ant.apache.org/ivy/) to resolve and download dependencies for Spark applications. By default,
each submission of a Spark application will provision a new Pod with an empty Ivy cache, meaning the dependencies have
to be re-downloaded for each submission. To improve this, the aiSSEMBLE Spark Operator Helm chart includes an option to
use a shared Ivy cache. When enabled, the chart will create a PersistentVolumeClaim and mount the volume as the Ivy
cache to the Spark Operator. This PVC can then be reused by all Spark applications by mounting the PVC as the Ivy cache
to both the driver and all executors. This can significantly improve the speed and reliability of Spark application
executions.

To disable the shared Ivy cache, set the `ivyCache.enabled` property to `false` and update the default volumes for the
Spark Operator to remove the Ivy cache PersistentVolumeClaim.  For example:

```yaml
aissemble-spark-operator-chart:
  ivyCache:
    enabled: false
  spark-operator:
    volumes:
      - name: spark-logging
        emptyDir: {}
      - name: ivy-cache
        emptyDir: {}
```

To enable the shared Ivy cache, set the `ivyCache.enabled` property to `true`. By default, the chart will only create a
PersistentVolumeClaim.  To ensure this PVC can be fulfilled, you can:
 1. Manually create a PersistentVolume that can satisfy the PVC's requirements
 2. Dynamically provision a PersistentVolume by setting the StorageClass in the `ivyCache.storageClass` property
 3. Set `ivyCache.storeCacheOnNode` to `true` to use a local directory on the Node as the Ivy cache

## Manually Creating a PersistentVolume
See the Kubernetes [PersistentVolume documentation](https://kubernetes.io/docs/concepts/storage/persistent-volumes/) for
more info.  The PersistentVolume must have enough capacity to satisfy the PVC's `ivyCache.size` property. Consider
creating this PersistentVolume through a custom Helm chart so it can be easily redeployed and managed alongside the
Spark Operator.

## Dynamically Provisioning a PersistentVolume
If your Kubernetes cluster has a StorageClass that can [dynamically provision](https://kubernetes.io/docs/concepts/storage/dynamic-provisioning/)
PersistentVolumes, you can set the `ivyCache.storageClass` property to the name of the StorageClass.  If you're using
Rancher Desktop and you don't want to enable cache persistence between deployments, it's possible to use Rancher's
Local Path Provisioner to dynamically create and destroy the cache by setting `ivyCache.storageClass` to `local-path`.

## Storing the Cache on the Node
This option is intended for use in development environments. By setting `ivyCache.storeCacheOnNode` to true, the chart
will create a PersistentVolume for you that maps to a local directory on the host machine of the Node. This directory
is defined by the `ivyCache.cachePathOnNode` property.  This allows the Ivy cache to be reused between deployments.
Because the default location is in the `/tmp` directory, the cache may still be cleared between container restarts (e.g.
when restarting Rancher Desktop).

### Considerations
As noted, the local storage option is intended for development environments.  This is due to the inherent security risk 
of allowing a pod to access the host's filesystem.  In many environments, [`hostPath` volumes](https://kubernetes.io/docs/concepts/storage/volumes/#hostpath)
are disabled by default for this reason.  Moreover, it's important to note that because hostPath and local volumes are
tied directly to a single Node, this functionality may not work as expected in mult-node environments.

It is possible to configure the [Pod Security admission controller](https://kubernetes.io/docs/concepts/security/pod-security-admission/)
to allow hostPath volumes, either across the board or for Spark Operator pods specifically.  Alternatively, a [`local`
volume](https://kubernetes.io/docs/concepts/storage/volumes/#local) can be used which alleviates most of the security
concerns as well as the single-node limitation. However, redeployments of the Spark Operator will not reuse the same
volume, meaning the cache will be lost between deployments.

# Migration from aiSSEMBLE v1 Helm Charts
If you are migrating from the v1 version of the Spark Operator chart, use the tables below to apply any existing customizations from the old chart 
to the new v2 chart. Given you have not updated any of the default values in your v1 chart, you are safe to delete it and use the new v2 chart without
having to migrate any of your v1 properties.

## Property Location
All properties listed below have been moved to the parent chart. If any of your current properties are set to the same default value, we recommend 
removing them from your values file entirely.

**Note**: *all new property locations must include the prefix `aissemble-spark-operator-chart.spark-operator`*

The following properties utilize the same name in the both the V1 and V2 charts:

| Property                                  | Default Value                                                                                   | Additional Notes                                                                           |
|-------------------------------------------|-------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------|
| replicaCount                              | 1                                                                                               |                                                                                            |
| image.repository                          | ghcr.io/boozallen/aissemble-spark-operator                                                      |                                                                                            |
| image.pullPolicy                          | IfNotPresent                                                                                    |                                                                                            |
| image.tag                                 | Chart.Version                                                                                   |                                                                                            |
| imagePullSecrets                          | []                                                                                              |                                                                                            |
| nameOverride                              | ""                                                                                              |                                                                                            |
| fullnameOverride                          | spark-operator                                                                                  |                                                                                            |
| rbac.createRole                           | true                                                                                            |                                                                                            |
| rbac.createClusterRole                    | true                                                                                            |                                                                                            |
| serviceAccounts.spark.create              | true                                                                                            |                                                                                            |
| serviceAccounts.spark.name                | spark                                                                                           |                                                                                            |
| serviceAccounts.spark.annotations         | {}                                                                                              |                                                                                            |
| serviceAccounts.sparkoperator.create      | true                                                                                            |                                                                                            |
| serviceAccounts.sparkoperator.name        | sparkoperator                                                                                   |                                                                                            |
| serviceAccounts.sparkoperator.annotations | {}                                                                                              |                                                                                            |
| controllerThreads                         | 10                                                                                              |                                                                                            |
| resyncInterval                            | 30                                                                                              |                                                                                            |
| uiService.enable                          | true                                                                                            |                                                                                            |
| ingressUrlFormat                          | ""                                                                                              |                                                                                            |
| logLevel                                  | 2                                                                                               |                                                                                            |
| securityContext                           | {}                                                                                              |                                                                                            |
| podSecurityContext                        | runAsUser: 185<br/>runAsGroup: 1000<br/>fsGroup: 1000<br/>fsGroupChangePolicy: "OnRootMismatch" | Additional security context is necessary for utilizing the Shared Ivy Cache detailed above |
| volumes                                   | `spark-logging=/tmp/spark-logging`, `ivy-cache=/home/spark/.ivy2`                               | Additional volume is necessary for utilizing the Shared Ivy Cache detailed above           |
| volumeMounts                              | `spark-logging=/tmp/spark-logging`, `ivy-cache=/home/spark/.ivy2`                               | Additional volume mount is necessary for utilizing the Shared Ivy Cache detailed above     |
| webhook.enable                            | true                                                                                            |                                                                                            |
| webhook.port                              | 8080                                                                                            |                                                                                            |
| webhook.namespaceSelector                 | ""                                                                                              |                                                                                            |
| webhook.timeout                           | 30                                                                                              |                                                                                            |
| metrics.enable                            | true                                                                                            |                                                                                            |
| metrics.port                              | 10254                                                                                           |                                                                                            |
| metrics.portName                          | metrics                                                                                         |                                                                                            |
| metrics.endpoint                          | /metrics                                                                                        |                                                                                            |
| metrics.prefix                            | ""                                                                                              |                                                                                            |
| podMonitor.enable                         | false                                                                                           |                                                                                            |
| podMonitor.labels                         | {}                                                                                              |                                                                                            |
| podMonitor.jobLabel                       | spark-operator-podmonitor                                                                       |                                                                                            |
| podMonitor.podMetricsEndpoint             | scheme: http<br/>interval: 5s                                                                   |
| nodeSelector                              | {}                                                                                              |                                                                                            |
| tolerations                               | []                                                                                              |                                                                                            |
| affinity                                  | {}                                                                                              |                                                                                            |
| podAnnotations                            | {}                                                                                              |                                                                                            |
| podLabels                                 | {}                                                                                              |                                                                                            |
| resources                                 | {}                                                                                              |                                                                                            |
| batchScheduler.enable                     | false                                                                                           |                                                                                            |
| resourceQuotaEnforcement.enable           | false                                                                                           |                                                                                            |
| leaderElection.lockName                   | spark-operator-lock                                                                             |                                                                                            |
| leaderElection.lockNamespace              | ""                                                                                              |                                                                                            |
| istio.enabled                             | false                                                                                           |                                                                                            |
| labelSelectorFilter                       | ""                                                                                              |                                                                                            |

The following properties have been renamed/moved to a new location in the V2 charts:

| Old Property Location | New Property Location | Default Value | Additional Notes                             |
|-----------------------|-----------------------|---------------|----------------------------------------------|
| sparkJobNamespace     | sparkJobNamespaces    | [""]          | The new attribute takes a list of namespaces |

## Property Removed
The following properties no longer exist.

| Property                   | Reason                                                                                      |
|----------------------------|---------------------------------------------------------------------------------------------|
| webhook.initAnnotations    | This property is not used in the new chart due to the webhook no longer using an init job   |
| webhook.cleanupAnnotations | This property is not used in the new chart due to the webhook no longer using a cleanup job |

## Additional Changes
The V2 chart introduces new functionality for utilizing a cache to save Spark Application dependencies, see the [Shared Ivy Cache](#shared-ivy-cache) and [Custom Properties](#custom-properties) sections for more details on utilizing the cache in this chart. Additionally, see the [Spark Application Helm Chart README](../aissemble-spark-application-chart/README.md#shared-ivy-cache) for details on utilizing the cache in your Spark Applications.
