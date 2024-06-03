# aiSSEMBLE&trade; Spark Thrift Helm Chart
Baseline Helm chart for deploying the Spark Thrift Server. Chart is built and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory. See https://github.com/kokuwaio/helm-maven-plugin for more details.

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart version with the desired aiSSEMBLE version. For example:
```bash
helm install thriftserver ghcr.io/boozallen/aissemble-thrift-server-chart --version <AISSEMBLE-VERSION>
```
_**NOTE:**_ *the version should match the aiSSEMBLE project version.*

# Properties

| Property                         | Description                                                                                                                       | Default                                                               |
|----------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------|
| enable                           | Enable or disable the entirety of the spark-thrift-server deployment.  When false, equivalent to not installing the chart.        | true                                                                  |
| deployment.annotations           | Annotations to apply to the Spark Thrift Server Deployment.                                                                       | {}                                                                    |
| deployment.labels                | Labels to apply to the Spark Thrift Server Deployment.                                                                            | {}                                                                    |
| deployment.replicas              | Number of replicas for the Spark Thrift Server Deployment.                                                                        | 1                                                                     |
| deployment.image.repository      | Repository for the Spark Thrift Server image.                                                                                     | "apache/spark"                                                        |
| deployment.image.tag             | Tag for the Spark Thrift Server image.                                                                                            | "3.5.1"                                                               |
| deployment.image.imagePullPolicy | Image pull policy for the Spark Thrift Server image.                                                                              | "IfNotPresent"                                                        |
| deployment.command               | Command to run in the container.                                                                                                  | `["/opt/spark/sbin/start-thriftserver.sh"]`                           |
| deployment.env                   | Environment variables to set in the Spark Thrift Server Deployment.                                                               | `SPARK_NO_DAEMONIZE: "true"`                                          |
| deployment.envFromSecret         | Environment variables to pull from a Secret. Format: <br/>`ENV_VAR.secretName: k8s_secret_name`<br/>`ENV_VAR.key: k8s_secret_key` | {}                                                                    |
| deployment.volumes               | Volumes to attach to the Spark Thrift Server Deployment.                                                                          | []                                                                    |
| deployment.volumeMounts          | Volume mounts to attach to the Spark Thrift Server Deployment.                                                                    | []                                                                    |
| dependencies.packages            | List of packages to install in the Spark Thrift Server Deployment.                                                                | []                                                                    |
| dependencies.jars                | List of jars to install in the Spark Thrift Server Deployment.                                                                    | []                                                                    |
| ingress.enabled                  | Enable or disable the Spark Thrift Server Ingress.                                                                                | false                                                                 |
| ingress.metadata.annotations     | Annotations to apply to the Spark Thrift Server Ingress.                                                                          | {}                                                                    |
| ingress.ingressClassName         | Ingress class to use for the Spark Thrift Server Ingress.                                                                         | "nginx"                                                               |
| ingress.hosts                    | Hosts to apply to the Spark Thrift Server Ingress.                                                                                | `[paths: []]`                                                         |
| service.annotations              | Annotations to apply to the Spark Thrift Server Service.                                                                          | {}                                                                    |
| service.type                     | Type of service to create for the Spark Thrift Server.                                                                            | "ClusterIP"                                                           |
| service.ports                    | Name of the service port.                                                                                                         | `[{name: "thrift", port: 10000}, {name: "thrift-http", port: 10001}]` |
| sparkConf                        | Configuration for the Spark Thrift Server.                                                                                        | ""                                                                    |
| hiveSite                         | Configuration for the Hive Site.                                                                                                  | ""                                                                    |
