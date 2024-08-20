# aiSSEMBLE&trade; Apache Kafka Helm Chart
Baseline Helm chart for packaging and deploying Kafka. Built on the [Bitnami Helm chart](https://bitnami.com/stack/kafka/helm) and managed during the normal Maven build lifecycle and placed in the **target/helm/repo** directory. See [Helm Maven Plugin](https://github.com/kokuwaio/helm-maven-plugin) for more details.

# Basic usage with Helm CLI
To use the module, perform [extension-helm setup](../README.md#leveraging-extensions-helm) and override the chart version with the desired aiSSEMBLE version. For example:
```bash
helm install kafka oci://ghcr.io/boozallen/aissemble-kafka-chart --version <AISSEMBLE-VERSION>
```
**Note**: *the version should match the aiSSEMBLE project version.*

# Properties

The following properties are inherited from the base [Kafka chart](https://github.com/bitnami/charts/blob/a2ab641dfad1a918d69959750819ab269ab12985/bitnami/kafka/README.md), but with updated default values.

| Property                    | Default                                                                                 |
|-----------------------------|-----------------------------------------------------------------------------------------|
| fullnameOverride            | kafka-cluster                                                                           |
| containerPorts.client       | 9093                                                                                    |
| containerPorts.controller   | 9097                                                                                    |
| listenerSecurityProtocolMap | CLIENT:PLAINTEXT,EXTERNAL:PLAINTEXT,CONTROLLER:PLAINTEXT                                |
| advertisedListeners         | CLIENT://kafka-cluster:9093,EXTERNAL://localhost:19092                                  |
| interBrokerListenerName     | CLIENT                                                                                  |
| listeners                   | - CLIENT://0.0.0.0:9093<br/> - EXTERNAL://0.0.0.0:9092<br/> - CONTROLLER://0.0.0.0:9097 |
| service.ports.client        | 9093                                                                                    |
| service.ports.external      | 19092                                                                                   |
| service.ports.controller    | 9097                                                                                    |

All properties must be prefixed with the key `aissemble-kafka-chart.kafka` to override any values in the chart. See [helm documentation](https://helm.sh/docs/chart_template_guide/subcharts_and_globals/#overriding-values-from-a-parent-chart) for more info.


# Migration from aiSSEMBLE v1 Helm Charts
If you are migrating from the v1 version of the kafka chart, use the tables below to apply any existing customizations from the old chart to the new v2 chart.
Note that because this new chart includes a zookeeper deployment, the version 1 chart for zookeeper is no longer needed.
If you've adopted the existing ZooKeeper chart for other purposes, it's possible to set `zookeeper.enabled` to `false`
and configure this chart to use your existing ZooKeeper deployment. Either way, it's recommended to remove the
`zookeeper-alert-deploy` Fermenter profile execution from your deploy POM as it is deprecated and will no longer be maintained.

## Property Location
All properties listed below have been moved to the parent chart. If any properties are set to the default value, we recommend removing them from your values file entirely.
Several properties that used to be free-form name/value pairs within the `env` block have become first class properties.
In the table below, the notation `env[KEY]` refers the `env` list item whose `name` value was `key`.


**Note**: *all new property locations include the prefix `aissemble-kafka-chart.kafka`*

| Old Property Location                     | New Property Location       | Additional Notes                                                                                                           |
|-------------------------------------------|-----------------------------|----------------------------------------------------------------------------------------------------------------------------|
| hostname                                  | fullnameOverride            |
| service.spec.ports                        | service.ports               |                                                                                                                            |
| env[KAFKA_ADVERTISED_LISTENERS]           | advertisedListeners         | change `INSIDE` to `CLIENT`, `OUTSIDE` to `EXTERNAL`.                                                                      |
| env[KAFKA_CREATE_TOPICS]                  | provisioning.topics         | This is now a YAML list, so each topic should be on it's own line                                                          |
| env[KAFKA_INTER_BROKER_LISTENER_NAME]     | interBrokerListenerName     | change `INSIDE` to `CLIENT`, `OUTSIDE` to `EXTERNAL`.                                                                      |
| env[KAFKA_LISTENERS]                      | listeners                   | change `INSIDE` to `CLIENT`, `OUTSIDE` to `EXTERNAL`, added `CONTROLLER://0.0.0.0:9097`                                    |
| env[KAFKA_LISTENER_SECURITY_PROTOCOL_MAP] | listenerSecurityProtocolMap | change `INSIDE:PLAINTEXT` to `CLIENT:PLAINTEXT`, `OUTSIDE:PLAINTEXT` to `EXTERNAL:PLAINTEXT`, added `CONTROLLER:PLAINTEXT` |
| deployment.resources.limits.memory        | resources.limits.memory     |                                                                                                                            |

## Property Removed
The following properties no longer exist.

| Property                    | Reason                                                                               |
|-----------------------------|--------------------------------------------------------------------------------------|
| replicaCount                | This property was ignored in the original chart by default                           |
| deployment.restartPolicy    | Defaults to `Always` and cannot be overridden                                        |
| deployment.ports            | Deployment ports are now inherited from service.ports                                |
| env.KAFKA_ZOOKEEPER_CONNECT | Default behavior in v2 uses Kafka without ZooKeeper, kraft uses controller port 9097 |
