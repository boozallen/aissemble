# aiSSEMBLE&trade; Pipeline Invocation Helm Chart
Baseline Helm chart for packaging and deploying a pipeline invocation service. Chart is built and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory. See https://github.com/kokuwaio/helm-maven-plugin for more details.

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart version with the desired aiSSEMBLE version. For example:
```bash
helm install pipeline-invocation-service oci://ghcr.io/boozallen/aissemble-pipeline-invocation-chart --version <AISSEMBLE-VERSION>
```

**Note**: *the version should match the aiSSEMBLE project version.*

# Properties
| Property                                                                | Description                                                                 | Required Override | Default                                             |
|-------------------------------------------------------------------------|-----------------------------------------------------------------------------|-------------------|-----------------------------------------------------|
| ingress.apiVersion                                                      | k8s API version to use                                                      | No                | networking.k8s.io/v1                                |
| ingress.enabled                                                         | k8s Whether to enable ingress                                               | No                | false                                               |
| ingress.kind                                                            | Type of kubernetes entity                                                   | No                | Ingress                                             |
| ingress.metadata.name                                                   | Name of the ingress                                                         | No                | pipeline-invocation-service-web                     |
| ingress.metadata.annotations.kubernetes.io/ingress.class                | Ingress class name                                                          | No                | nginx                                               |
| ingress.metadata.annotations.nginx.ingress.kubernetes.io/server-snippet | Custom configurations for the nginx ingress class                           | No                | gunzip on; gzip on; gzip_proxied any; gzip_types *; |
| ingress.spec.rules.hosts                                                | A list of hosts for ingress to support, each with their own path definition | No                |                                                     |
| ingress.status                                                          | Load balancer IP if required                                                | No                | None                                                |
| rbac.createClusterRole                                                  | Create and use RBAC `ClusterRole` resources.                                | No                | true                                                |

# Quarkus Configuration

The following configuration of the service is provided.  Additional configuration may be provided in accordance with [Quarkus](https://quarkus.io/guides/all-config) documentation.


| Property                                    | Description                                                                                                                                                   | Accepted Values                                                               |
|---------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------|
| kafka.bootstrap.servers                     | Specifies the kafka bootstrap server when using kafka for messaging                                                                                           | Any valid URI                                                                 |
| mp.messaging.incoming.pipeline-invocation.* | Specifies and configures the smallrye connector to use.  Supported connectors are `smallrye-amqp`, `smallrye-kafka`, `smallrye-mqtt`, and `smallrye-rabbitmq` | See xref:messaging-details.adoc[the Messaging documentation] for more details |


