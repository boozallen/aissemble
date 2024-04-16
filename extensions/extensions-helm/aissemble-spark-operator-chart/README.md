# aiSSEMBLE&trade; Spark Operator Helm Chart
Baseline Helm chart for deploying the Spark Kubernetes Operator. Chart is built and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory. See https://github.com/kokuwaio/helm-maven-plugin for more details.

This chart is based heavily upon the original spark-On-k8s-operator upstream chart, available at https://github.com/kubeflow/spark-operator/blob/master/charts/spark-operator-chart/README.md.

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart version with the desired aiSSEMBLE version. For example:
```bash
helm install spark-operator ghcr.io/boozallen/aissemble-spark-operator-chart --version <AISSEMBLE-VERSION>
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
| namespaceOverride         | Namespace to deploy to instead of the Helm release namespace                                                    | .Release.Namespace   |

## Overridden Inherited Properties
For an exhaustive list of available properties, see the source material at https://github.com/kubeflow/spark-operator/blob/master/charts/spark-operator-chart/README.md#values.
What follows reflects properties in the base spark-operator chart which have been overridden.  To modify these properties,
you must reference the root chart in your `values.yaml` file.  For example:

```yaml
aissemble-spark-operator:
  spark-operator:
    webhook.enable: false
```

| Property                   | Description                        | Required Override | Default                                                                               |
|----------------------------|------------------------------------|-------------------|---------------------------------------------------------------------------------------|
| image.repository           | The image repository               | No                | NB: OSS: update with aissemble docker repository/boozallen/aissemble-spark-operator |
| image.tag                  | The image tag                      | No                | Chart.Version                                                                         |
| webhook.enable             | Enable webhook server              | No                | true                                                                                  |
| volumes                    | Volumes for the pod                | No                | `spark-logging=/tmp/spark-logging`                                                    |
| volumeMounts               | Volume Mounts for the pod          | No                | `spark-logging=/tmp/spark-logging`                                                    |
| fullnameOverride           | String to override release name    | No                | spark-operator                                                                        |
| rbac.createClusterRole     | See `Migrated Properties`          | No                | false                                                                                 |
| serviceAccounts.spark.name | Name for the spark service account | No                | spark                                                                                 |


## Migrated Properties
The following properties have been migrated from the `spark-operator` subchart to the `aissemble-spark-operator` chart.
Any required overrides should be cognisant of the alternate path.  For example:

```yaml
aissemble-spark-operator:
  rbac:
    createClusterRole: false
```

| Property               | Description                                                                   | Default |
|------------------------|-------------------------------------------------------------------------------|---------|
| rbac.createClusterRole | Create and use RBAC `ClusterRole` resources.  Migrated to use modified rules. | true    |


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
aissemble-spark-operator:
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
