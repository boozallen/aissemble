# aiSSEMBLE&trade; Spark History Helm Chart
Baseline Helm chart for deploying the Spark History Server. Chart is built and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory. See https://github.com/kokuwaio/helm-maven-plugin for more details.

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart version with the desired aiSSEMBLE version. For example:
```bash
helm install spark-operator ghcr.io/boozallen/aissemble-spark-history-chart --version <AISSEMBLE-VERSION>
```
_**NOTE:**_ *the version should match the aiSSEMBLE project version.*

# Properties

| Property                         | Description                                                                                                                       | Default                                                                                                                  |
|----------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| enable                           | Enable or disable the entirety of the spark-history-server deployment.  When false, equivalent to not installing the chart.       | true                                                                                                                     |
| deployment.annotations           | Annotations to apply to the Spark History Server Deployment.                                                                      | {}                                                                                                                       |
| deployment.labels                | Labels to apply to the Spark History Server Deployment.                                                                           | {}                                                                                                                       |
| deployment.replicas              | Number of replicas for the Spark History Server Deployment.                                                                       | 1                                                                                                                        |
| deployment.image.repository      | Repository for the Spark History Server image.                                                                                    | "apache/spark"                                                                                                           |
| deployment.image.tag             | Tag for the Spark History Server image.                                                                                           | "3.5.1"                                                                                                                  |
| deployment.image.imagePullPolicy | Image pull policy for the Spark History Server image.                                                                             | "IfNotPresent"                                                                                                           |
| deployment.command               | Command to run in the container.                                                                                                  | `["/opt/spark/sbin/start-spark-history-server.sh"]`                                                                      |
| deployment.env                   | Environment variables to set in the Spark History Server Deployment.                                                              | `SPARK_NO_DAEMONIZE: "true"`                                                                                             |
| deployment.envFromSecret         | Environment variables to pull from a Secret. Format: <br/>`ENV_VAR.secretName: k8s_secret_name`<br/>`ENV_VAR.key: k8s_secret_key` | {}                                                                                                                       |
| deployment.volumes               | Volumes to attach to the Spark History Server Deployment.                                                                         | []                                                                                                                       |
| deployment.volumeMounts          | Volume mounts to attach to the Spark History Server Deployment.                                                                   | []                                                                                                                       |
| dependencies.packages            | List of packages to install in the Spark History Server Deployment.                                                               | []                                                                                                                       |
| dependencies.jars                | List of jars to install in the Spark History Server Deployment.                                                                   | []                                                                                                                       |
| ingress.enabled                  | Enable or disable the Spark History Server Ingress.                                                                               | true                                                                                                                     |
| ingress.metadata.annotations     | Annotations to apply to the Spark History Server Ingress.                                                                         | {}                                                                                                                       |
| ingress.ingressClassName         | Ingress class to use for the Spark History Server Ingress.                                                                        | "nginx"                                                                                                                  |
| ingress.hosts                    | Hosts to apply to the Spark History Server Ingress.                                                                               | `[paths: [{path: "/", pathType: "Prefix", backend: {service: {name: "spark-history-service", port: {number: 18080}}}}]]` |
| service.annotations              | Annotations to apply to the Spark History Server Service.                                                                         | {}                                                                                                                       |
| service.type                     | Type of service to create for the Spark History Server.                                                                           | "LoadBalancer"                                                                                                           |
| service.port.name                | Name of the service port.                                                                                                         | "shs-http"                                                                                                               |
| service.port.port                | Port number for the service.                                                                                                      | 18080                                                                                                                    |
| eventVolume.enabled              | Enable or disable the default Event Volume for the Spark History Server.                                                          | false                                                                                                                    |
| eventVolume.name                 | Name of the Event Volume.                                                                                                         | "spark-events"                                                                                                           |
| eventVolume.mountPath            | Mount path for the Event Volume.                                                                                                  | "/tmp/spark-events"                                                                                                      |
| eventVolume.storageType          | Type of storage to use for the Event Volume. Legal values: `local`, `custom`                                                      | "local"                                                                                                                  |
| eventVolume.size                 | Size of the Event Volume.                                                                                                         | "1Gi"                                                                                                                    |
| eventVolume.accessModes          | Access modes for the Event Volume.                                                                                                | ["ReadWriteMany"]                                                                                                        |
| eventVolume.mountOptions         | Mount options for the Event Volume.                                                                                               | ["allow-delete"]                                                                                                         |
| eventVolume.volumePathOnNode     | Path on the underlying node to mount the Event Volume.                                                                            | "/tmp"                                                                                                                   |
| sparkConf                        | Configuration for the Spark History Server.                                                                                       | ""                                                                                                                       |


## Manually Creating a PersistentVolume
See the Kubernetes [PersistentVolume documentation](https://kubernetes.io/docs/concepts/storage/persistent-volumes/) for
more info.  The PersistentVolume must have enough capacity to satisfy the PVC's `eventVolume.size` property. Consider
creating this PersistentVolume through a custom Helm chart so it can be easily redeployed and managed alongside the
Spark History Server.  Additionally, if attempting to use the premade `PersistentVolumeClaim`, the `eventVolume.storageType`
must be set to `custom`, and the `PersistentVolume` should be named `[eventVolume.name]-pv`.

## Dynamically Provisioning a PersistentVolume
If your Kubernetes cluster has a StorageClass that can [dynamically provision](https://kubernetes.io/docs/concepts/storage/dynamic-provisioning/)
PersistentVolumes, you can set the `eventVolume.storageClass` property to the name of the StorageClass.  If you're using
Rancher Desktop and you don't want to enable cache persistence between deployments, it's possible to use Rancher's
Local Path Provisioner to dynamically create and destroy the cache by setting `eventVolume.storageClass` to `local-path`
and `eventVolume.storageType` to `custom`.

## Storing the Cache on the Node
This option is intended for use in development environments. By setting `ivyCache.storeCacheOnNode` to true, the chart
will create a PersistentVolume for you that maps to a local directory on the host machine of the Node. This directory
is defined by the `eventVolume.volumePathOnNode` property.  This allows the Ivy cache to be reused between deployments.
Because the default location is in the `/tmp` directory, the cache may still be cleared between container restarts (e.g.
when restarting Rancher Desktop).

### Considerations
As noted, the local storage option is intended for development environments.  This is due to the inherent security risk 
of allowing a pod to access the host's filesystem.  In many environments, [`hostPath` volumes](https://kubernetes.io/docs/concepts/storage/volumes/#hostpath)
are disabled by default for this reason.  Moreover, it's important to note that because hostPath and local volumes are
tied directly to a single Node, this functionality may not work as expected in mult-node environments.

It is possible to configure the [Pod Security admission controller](https://kubernetes.io/docs/concepts/security/pod-security-admission/)
to allow hostPath volumes, either across the board or for Spark History Server pods specifically.  Alternatively, a [`local`
volume](https://kubernetes.io/docs/concepts/storage/volumes/#local) can be used which alleviates most of the security
concerns as well as the single-node limitation. However, redeployments of the Spark Operator will not reuse the same
volume, meaning the cache will be lost between deployments.
